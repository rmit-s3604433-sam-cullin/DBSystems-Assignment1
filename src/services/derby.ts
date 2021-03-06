import { Entity, IDbService, InitResult } from "../types";
import JDBC from 'jdbc';
import { Logger } from "./logger";
import { IJDBC } from "../@types/JDBC/pool";
import { Connection, Statement } from "../@types/JDBC/connection";
import * as jinst from 'jdbc/lib/jinst';

export class DerbyServiceConfig {
    url: string;
    dbName: string;
    tableName: string;
    derbyDrivers: string[];

    constructor(public readonly optimize: boolean = false) {
        this.url = process.env.DERBY_URL;
        this.dbName = process.env.DERBY_DB;
        this.tableName = process.env.DERBY_TABLE;
        this.derbyDrivers = process.env.DERBY_DRIVERS.split(';');
    }
}


class DerbyEntity extends Entity {


    public InsertValues(): string {
        return `( ${this.id} , '${this.dateTime.trim()}' , ${this.year}, ${this.mDate}, '${this.month.trim()}', '${this.day.trim()}', ${this.time}, ${this.sensorId}, '${this.sensorName.trim()}' , ${this.hourlyCount} )`;
    }
    public Insert(table: string): string {
        return `${DerbyEntity.InsertPrefix(table)}${this.InsertValues()};`;
    }

    public static InsertPrefix(table: string): string {
        return `INSERT INTO ${table} (id , dateTime, mYear , mDate, mMonth, day , time, sensorId , sensorName , hourlyCount ) VALUES `
    }
    public static Create(obj: Entity): DerbyEntity {
        const derbyEnt = new DerbyEntity();
        return Object.assign(derbyEnt, obj);
    }

    public static BulkInsert(table: string, data: Entity[]) {
        let sql = DerbyEntity.InsertPrefix(table);
        let token = ""
        data.reduce((reducer, value) => {
            sql += token + DerbyEntity.Create(value).InsertValues();
            token = ","
            return sql
        }, sql)
        return sql //+ ";"
    }



    public static CreateIdIndex(table: string): string {
        // NOTE: Unique index could only write about 60'000 before slowing down to an unusable level tried with batch size 100 and 1000
        return `CREATE INDEX index_${table}_id on ${table}(id)`
    }

    public static CreateDateTimeIndex(table: string): string {
        return `CREATE INDEX index_${table}_datetime on ${table}(dateTime)`
    }

    public static DropIdIndex(table: string): string {
        return `DROP INDEX index_${table}_id`
    }

    public static DropDateTimeIndex(table: string): string {
        return `DROP INDEX index_${table}_datetime`
    }




    public static CreateTable(table: string): string {
        return `CREATE TABLE ${table} (id int, dateTime VARCHAR(22), mYear int, mDate int, mMonth VARCHAR(9), day VARCHAR(9),time int,  sensorId int, sensorName VARCHAR(39), hourlyCount int )`
    }

    public static DropTable(table: string) {
        return `DROP TABLE ${table}`;
    }

    public Query(table: string): string {
        return `Select * from ${table} where id = ${this.id}`;
    }
}


export class DerbyDbService implements IDbService {
    private readonly logger: Logger = new Logger(DerbyDbService.name);
    private readonly client: IJDBC;
    private conn: Connection;

    constructor(
        private readonly config: DerbyServiceConfig = new DerbyServiceConfig()
    ) {
        if (!jinst.isJvmCreated()) {
            // Add all java options required by your project here.  You get one chance to
            // setup the options before the first java call.
            jinst.addOption("-Xrs");
            // Add all jar files required by your project here.  You get one chance to
            // setup the classpath before the first java call.
            jinst.setupClasspath(this.config.derbyDrivers);
        }
        this.client = new JDBC({
            url: `${this.config.url}${this.config.dbName};create=true`,
            //dirvername: 'example.jdbc.DriverName',
            //user: this.config.user,
            //password: this.config.password
        });
    }
    queryId(id: any): Promise<Entity> {
        return this.getStatement().then((statement) => {
            let entity = new DerbyEntity();
            entity.id = id;
            return new Promise((resolve, reject) => {
                statement.executeQuery(entity.Query(this.config.tableName), (err, resultset) => {
                    if (err) {
                        return reject(err);
                    }
                    resultset.toObjArray((error, result) => {
                        if (error) {
                            return reject(error)
                        }
                        if (result.length == 0) {
                            return reject(new Error("Did not find the object"))
                        }
                        return resolve(result[0])
                    })
                })
            })
        })
    }

    async clean(): Promise<any> {
        return this.getStatement().then(async (statement) => {
            await new Promise((resolve, reject) => {
                statement.executeUpdate(DerbyEntity.DropTable(this.config.tableName), (err, count) => {
                    if (err && !err.message.includes('does not exist')) {
                        this.logger.error(`Error DropTable ${this.config.tableName} `, err);
                        return reject(err);
                    }
                    this.logger.info(`Dropped table ${this.config.tableName}`)
                    return resolve(count)
                })
            })
            await new Promise((resolve, reject) => {
                statement.executeUpdate(DerbyEntity.DropIdIndex(this.config.tableName), (err, count) => {
                    if (err && !err.message.includes('does not exist')) {
                        this.logger.error(`Error DropIdIndex ${this.config.tableName} `, err);
                        return reject(err);
                    }
                    this.logger.info(`Dropped Index Id ${this.config.tableName}`)
                    return resolve(count)
                })
            })
            await new Promise((resolve, reject) => {
                statement.executeUpdate(DerbyEntity.DropDateTimeIndex(this.config.tableName), (err, count) => {
                    if (err && !err.message.includes('does not exist')) {
                        this.logger.error(`Error DropDateTimeIndex ${this.config.tableName} `, err);
                        return reject(err);
                    }
                    this.logger.info(`Dropped Index DateTime ${this.config.tableName}`)
                    return resolve(count)
                })
            })
        })
    }


    private async getConnection(): Promise<Connection> {
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
                this.conn = conn.conn;
                resolve(this.conn);
            })
        })
    }

    private async getStatement(): Promise<Statement> {
        return this.getConnection().then(conn => {
            return new Promise((resolve, reject) => {
                conn.createStatement((err, statement) => {
                    if (err) {
                        this.logger.error("Failed To create statement", err);
                        return reject(err);
                    }
                    return resolve(statement);
                })
            })
        })
    }




    private async initOptimization(): Promise<InitResult> {
        const idIndex: InitResult = await this.getStatement().then(statement => {
            return new Promise((resolve, reject) => {
                const sql = DerbyEntity.CreateIdIndex(this.config.tableName);
                statement.executeUpdate(sql, (error, result) => {
                    if (error) {
                        this.logger.info("Error Creating Id Index sql: ", sql);
                        this.logger.error("Error creating Id Index ", error);
                        return reject({ success: false, error })
                    }
                    return resolve({ success: true })
                })

            })
        })
        if (!idIndex.success) {
            return idIndex;
        }
        const dateTimeIndex: InitResult = await this.getStatement().then((statement) => {
            return new Promise((resolve, reject) => {
                const sql = DerbyEntity.CreateDateTimeIndex(this.config.tableName);
                statement.executeUpdate(sql, (error, result) => {
                    if (error) {
                        this.logger.info("Error Creating DateTime Index sql: ", sql);
                        this.logger.error("Error creating DateTime Index ", error);
                        return reject({ success: false, error })
                    }
                    return resolve({ success: true })
                })

            })
        })
        return dateTimeIndex;

    }

    private async initConnection(): Promise<InitResult> {
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

    private async initTable(): Promise<InitResult> {
        return this.getStatement().then((statement) => {
            return new Promise((resolve, reject) => {
                const sql = DerbyEntity.CreateTable(this.config.tableName)
                statement.executeUpdate(sql, (error, result) => {
                    if (error && !error.message.includes('already exists')) {
                        this.logger.info("Error Creating table sql: ", sql);
                        this.logger.error("Error creating table ", error);
                        return reject({ success: false, error });
                    }
                    this.logger.info("Created table");
                    return resolve({ success: true })
                })
            })
        })
    }


    async init(): Promise<InitResult> {
        this.logger.info("Init Connection ", this.config);
        let result = await this.initConnection();
        if (!result.success) {
            return result;
        }
        result = await this.initTable()
        if (!result.success) {
            return result;
        }

        if (this.config.optimize) {
            result = await this.initOptimization();
        }

        return result;
    }


    async save(data: Entity[]) {
        return this.getStatement().then(statement => {
            return new Promise((resolve, reject) => {
                const sql = DerbyEntity.BulkInsert(this.config.tableName, data)
                statement.executeUpdate(
                    sql,
                    (err, result) => {
                        if (err) {
                            this.logger.info(`SQL`, sql);
                            this.logger.error(`Error writing objects`, err);
                            return reject(err);
                        }
                        return resolve(result);
                    })
            })
        })
    }


    async denit() {
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
    }

}