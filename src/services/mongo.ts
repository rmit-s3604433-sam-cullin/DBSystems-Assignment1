import { DataFile } from "../datafile";
import { Collection, Db, MongoClient } from 'mongodb';
import { Entity, IDbService } from "../types";
import { Logger } from "./logger";

export class MongoServiceConfig {
    url: string;
    dbName: string;
    tableName: string;

    constructor() {
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

    async init() {
        this.logger.info("Init Connection ", this.config);
        await this.getCollection();
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
        return this.getCollection().then((collection) => {
            return collection.drop().catch((err) => {
                this.logger.error("Clean DB ", err)
            })
        })
    }


    async denit() {
        return this.client.close();
    }

}