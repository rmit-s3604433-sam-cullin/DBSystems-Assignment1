import { DataFile } from "./datafile";
import { IDbService } from "./types";
import { bufferCount, concatMap, map, max, mergeAll, mergeMap, share, switchMap, take, toArray, windowCount } from 'rxjs/operators';
import { combineLatest, from, of, zip } from "rxjs";
import { Logger } from "./services/logger";
export interface ProcessorConfig {
    batchSize: number;
    limit: number;
}

export class Processor {
    private readonly logger: Logger = new Logger(Processor.name);
    constructor(
        private readonly config: ProcessorConfig,
        private readonly fileStream: DataFile,
        private readonly dbService: IDbService
    ) { }

    async init() {
        return this.dbService.init()
    }

    save() {
        this.logger.info('Starting the file stream ', this.config)
        return this.fileStream.$data.pipe(
            this.config.limit > 0 ? take(this.config.limit) : map(x => x),
            this.config.batchSize > 0 ? bufferCount(this.config.batchSize) : toArray(),
            concatMap((rows) => {
                return from(this.dbService.save(rows).then(result => {
                    return [rows, result]
                }));
            }),
            map(([rows, result]) => {
                return {
                    rows,
                    result
                }
            })
        )
    }

    // Used to scan and find entity sizes
    // run() {
    //     return this.fileStream.$data.pipe(
    //         take(this.config.limit),
    //         map((x: any) => {
    //             return {
    //                 size: Object.keys(x).reduce((rec: number, key: string) => {
    //                     rec += typeof x[key] == "string" ? Buffer.from(x[key]).byteLength : Math.ceil(x[key].toString(16).length / 2)
    //                     return rec;
    //                 }, 0),
    //                 sizeString: Object.keys(x).reduce((rec: number, key: string) => {
    //                     rec += Buffer.from(String(x[key])).byteLength
    //                     return rec;
    //                 }, 0),
    //                 data: x
    //             }
    //         }),
    //         max((x, y) => {
    //             return x.size - y.size
    //         }),
    //         map((x) => {
    //             let data: any = x.data;
    //             return Object.keys(data).reduce((rec: any, x) => {
    //                 rec[x] = {
    //                     data: data[x],
    //                     size: typeof data[x] == "string" ? Buffer.from(data[x]).byteLength : Math.ceil(data[x].toString(16).length / 2),
    //                     sizeString: Buffer.from(String(data[x])).byteLength,
    //                     hex: typeof data[x] == "string" ? Buffer.from(data[x]).toString('hex') : data[x].toString(16),
    //                     type: typeof data[x]
    //                 }

    //                 return rec;
    //             }, {
    //                 total: x.size,
    //                 totalString: x.sizeString
    //             })
    //         })
    //     )
    // }

    async denit(): Promise<any> {
        return this.dbService.denit();
    }
}