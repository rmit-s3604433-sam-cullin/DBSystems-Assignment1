import parse from "csv-parse";
import { createReadStream } from "fs";
import { Observable } from "rxjs";
import { Logger } from "./services/logger";
import { Entity } from "./types";



export class DataFile {
    private readonly logger: Logger = new Logger(DataFile.name);
    public readonly $data: Observable<Entity>;
    constructor(public readonly file: string) {
        this.logger.info(`Creating Data Source ${this.file}`)
        this.$data = new Observable((subscriber) => {
            const readStream = createReadStream(this.file)
            readStream
                .pipe(parse({ from: 2, delimiter: ',' }))
                .on('data', (row) => {
                    const entity = Entity.create(row)
                    subscriber.next(entity)
                })
                .on('error', (err) => {
                    this.logger.error("There was and error in the data source ", err)
                })
                .on('end', () => {
                    subscriber.complete();
                })
            return () => {
                readStream.close();
            }
        })
    }

}