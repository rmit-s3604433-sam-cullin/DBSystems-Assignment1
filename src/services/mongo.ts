import { DataFile } from "../datafile";
import { MongoClient } from 'mongodb';
import { IDbService } from "../types";
import { Logger } from "./logger";

export class MongoServiceConfig {
    url: string;
    dbName: string;

    constructor() {
        this.url = process.env.MONGO_URL;
        this.dbName = process.env.MONGO_DB;
    }
}




export class MongoDbService implements IDbService {
    private readonly client: MongoClient;
    private readonly logger: Logger = new Logger(MongoDbService.name);
    constructor(
        private readonly config: MongoServiceConfig = new MongoServiceConfig()
    ) {
        this.client = new MongoClient(this.config.url);
    }


    private async openConnection() {
        return this.client.connect().finally(() => {
            this.client.close();
        })
    }

    private async getDb() {
        return this.openConnection().then(client => {
            return client.db(this.config.dbName);
        })
    }


    private async getCollection(name: string) {
        return this.getDb().then((db => {
            const collection = db.collection(name);
            return collection
        }))
    }

    async init() {
        this.logger.info("Init Connection ", this.config);
        return Promise.resolve({ success: true });
    }


    async save(collectionName: string, data: any[]) {
        return this.getCollection(collectionName).then(collection => {
            return new Promise((resolve, reject) => {
                collection.bulkWrite(data, (err, res) => {
                    if (err) {
                        return reject(err)
                    }
                    resolve(res);
                })
            })
        })
    }


    async denit() {
        this.getDb().then(db => {
            return db.dropDatabase()
        })
    }

}