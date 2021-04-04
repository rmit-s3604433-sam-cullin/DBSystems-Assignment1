import { DataFile } from "../datafile";
import { Collection, Db, MongoClient } from 'mongodb';
import { Entity, IDbService } from "../types";
import { Logger } from "./logger";

export class MongoServiceConfig {
    url: string;
    dbName: string;
    tableName: string;

    constructor(public readonly optimize: boolean = false) {
        this.url = process.env.MONGO_URL;
        this.dbName = process.env.MONGO_DB;
        this.tableName = process.env.MONGO_TABLE;
    }
}




export class MongoDbService implements IDbService {
    private client: MongoClient;
    private db: Db;
    private collection: Collection<Entity>;
    private readonly logger: Logger = new Logger(MongoDbService.name);
    constructor(
        private readonly config: MongoServiceConfig = new MongoServiceConfig()
    ) {
    }


    private async getClient(): Promise<MongoClient> {
        if (this.client) {
            return Promise.resolve(this.client);
        }
        return new Promise((resolve, reject) => {
            MongoClient.connect(this.config.url, (error, client) => {
                if (error) {
                    this.logger.error("Error Creating Connection", error)
                    return reject(error)
                }
                this.client = client;
                return resolve(this.client);
            })
        })
    }

    private async getDb() {
        if (this.db) {
            return Promise.resolve(this.db);
        }
        return this.getClient().then(client => {
            this.db = client.db(this.config.dbName);
            return this.db
        })
    }


    private async getCollection() {
        if (this.collection) {
            return Promise.resolve(this.collection)
        }
        return this.getDb().then((db => {
            this.collection = db.collection(this.config.tableName);
            return this.collection
        }))
    }

    private async optimize() {
        this.logger.info(`Optimizing ...`)
        return this.getCollection().then((collection) => {
            return new Promise((resolve, reject) => {
                collection.createIndex({
                    "id": 1 // creates an ascending ordered index on column id  NOTE: cannot use hashed index when using unique options  https://docs.mongodb.com/manual/reference/method/db.collection.createIndex/#unique
                }, {
                    unique: true,
                    name: `index_${this.config.tableName}_id`,

                }, (error, result) => {
                    if (error) {
                        this.logger.error(`Creating ID Index `, error);
                        return reject(error);
                    }
                    this.logger.info(`Created ID Index`)
                    return resolve(result);
                })
            }).then(x => {
                return new Promise((resolve, reject) => {
                    collection.createIndex({
                        "dateTime": 1 // creates an ascending ordered index on column dateTime
                    }, {
                        unique: false,
                        name: `index_${this.config.tableName}_dateTime`,

                    }, (error, result) => {
                        if (error) {
                            this.logger.error(`Creating Date Time Index `, error);
                            return reject(error);
                        }
                        this.logger.info(`Created Date Time Index`)
                        return resolve(result);
                    })
                })
            })
        })
    }

    async init() {
        this.logger.info("Init Connection ", this.config);
        await this.getCollection();
        if (this.config.optimize) {
            await this.optimize();
        }
        this.logger.info("Init Connected status:", this.client.isConnected())
        return { success: true }
    }


    async save(data: any[]) {
        return this.getCollection().then(collection => {
            return new Promise((resolve, reject) => {
                collection.insertMany(data, (err, res) => {
                    if (err) {
                        return reject(err)
                    }
                    resolve(res);
                })
            })
        })
    }

    async queryId(id: number): Promise<Entity> {
        return this.getCollection().then((collection) => {
            return new Promise((resolve, reject) => {
                collection.findOne({ id: id }, (error, entity) => {
                    if (error) {
                        return reject(error);
                    }
                    return resolve(entity)
                });
            })
        })
    }



    async clean() {
        return this.getCollection().then(async (collection) => {
            await collection.drop().catch((err) => {
                this.logger.error("Clean DB ", err)
            })

            await collection.dropIndex(`index_${this.config.tableName}_id`)

            await collection.dropIndex(`index_${this.config.tableName}_dateTime`)
        })
    }


    async denit() {
        return this.client.close();
    }

}