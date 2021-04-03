import { Entity, IDbService, InitResult } from "../types";
import * as JDBC from 'jdbc';
import { Logger } from "./logger";
import { IJDBC } from "../@types/JDBC/pool";
import { Connection, Statement } from "../@types/JDBC/connection";

export class DerbyServiceConfig {
    url: string;
    dbName: string;
    tableName: string;

    constructor() {
        this.url = process.env.DERBY_URL;
        this.dbName = process.env.DERBY_DB;
        this.tableName = process.env.DERBY_TABLE;
    }
}


class DerbyEntity extends Entity {


    public InsertValues(): string {
        return `( ${this.id} , ${this.dateTime}, ${this.year}, ${this.mDate}, ${this.month}, ${this.day}, ${this.time}, ${this.sensorId}, ${this.sensorName}, ${this.hourlyCount} )`;
    }
    public Insert(table: string): string {
        return `${DerbyEntity.InsertPrefix(table)}${this.InsertValues()};`;
    }

    public static InsertPrefix(table: string): string {
        return `INSERT INTO ${table} VALUES `
    }

    public static BulkInsert(table: string, data: DerbyEntity[]) {
        let sql = DerbyEntity.InsertPrefix(table);
        data.reduce((reducer, value) => {
            sql += value.InsertValues();
            return sql
        }, sql)
        return sql + ";"
    }

    public static DropTable(table: string) {
        return `DROP TABLE ${table};`;
    }



    public static CreateTable(table: string): string {
        return `CREATE TABLE ${table} (id int, dateTime TIMESTAMP, year int, mDate int, month VARCHAR(9), day VARCHAR(9), sensorId int, sensorName VARCHAR(39), hourlyCount int );`
    }

    public Query(table: string): string {
        return `Select * from ${table} where id = ${this.id}`;
    }
}


export class DerbyDbService implements IDbService {
    private readonly logger: Logger = new Logger(DerbyDbService.name);
    private readonly client: IJDBC;
    private conn: any;

    constructor(
        private readonly config: DerbyServiceConfig = new DerbyServiceConfig()
    ) {
        this.client = new JDBC({
            url: `${this.config.url}${this.config.dbName};create=true`,
            //dirvername: 'my.jdbc.DriverName',
            //user: this.config.user,
            //password: this.config.password
        });
    }


    private async getConnection(): Promise<Connection> {
        this.logger.info("getConnection() ");
        return new Promise((resolve, reject) => {
            if (this.conn) {
                return resolve(this.conn)
            }
            this.client.reserve((err, conn) => {
                if (err) {
                    this.logger.error("Error Opening Connection", err);
                    reject(err);
                }
                this.logger.info("Opened Connection ", conn.uuid);

                conn.setSchema("test", (err) => {
                    if (err) {
                        this.logger.error("Error Setting Schema", err);
                        reject(err);
                    }
                    this.logger.info("Set Schema");
                    this.conn = conn;
                    resolve(this.conn);
                })
            })
        })
    }

    private async createStatement(): Promise<Statement> {
        this.logger.info("createStatement() ");
        return this.getConnection().then(conn => {
            return new Promise((resolve, reject) => {
                conn.createStatement((err, statement) => {
                    if (err) {
                        this.logger.error("Failed To create statement", err);
                        return reject(err);
                    }
                    this.logger.info("Created Statement");
                    return resolve(statement);
                })
            })
        })
    }


    async init() {
        this.logger.info("Init Connection ", this.config);
        return new Promise<InitResult>((resolve, reject) => {
            this.client.initialize((err) => {
                if (err) {
                    this.logger.error("Error Initializing Client", err);
                    return reject({ success: false, error: err });
                }
                this.logger.info("Successfully Connected");
                return resolve({ success: true });
            })
        });
    }


    async save(collectionName: string, data: DerbyEntity[]) {
        return this.createStatement().then(statement => {
            return new Promise((resolve, reject) => {
                statement.executeUpdate(
                    DerbyEntity.BulkInsert(this.config.tableName, data),
                    (err, result) => {
                        if (err) {
                            this.logger.error(`Error writing objects`, err);
                            return reject(err);
                        }
                        this.logger.info(`Saved ${data.length} items to ${this.config.tableName} `);
                        return resolve(data);
                    })
            })
        })
    }


    async denit() {
        this.createStatement().then((statement) => {
            return new Promise((resolve, reject) => {
                statement.executeUpdate(DerbyEntity.DropTable(this.config.tableName), (err, count) => {
                    if (err) {
                        this.logger.error(`Error Dropping Table ${this.config.tableName} `, err);
                        return reject(err);
                    }
                    this.logger.info(`Dropped table ${this.config.tableName}`)
                    return resolve(count)
                })
            })
        }).then((count) => {
            return new Promise((resolve, reject) => {
                if (this.conn) {
                    this.client.release(this.conn, (err) => {
                        if (err) {
                            this.logger.error("Could not release connection ", err);
                            return reject({ success: false, error: err })
                        }
                        this.logger.info("Released Connection");
                        this.conn = null;
                        return resolve({ success: true })
                    })
                }
                this.logger.info("No Connection to release");
                return resolve({ success: true })
            })
        })
    }

}