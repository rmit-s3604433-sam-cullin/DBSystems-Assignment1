import { DataFile } from "./datafile";
import { MockDbService } from "./services/mock";
import { MongoDbService } from "./services/mongo";
import { Processor } from "./processor";
import { IDbService, Type } from "./types";
import { DerbyDbService } from "./services/derby";
import * as dotenv from 'dotenv'
import * as yargs from 'yargs';
import { alias } from "yargs";
import { Logger } from "./services/logger";
dotenv.config();



const DBServices: { [key: string]: Type<IDbService> } = {
    ['mock']: MockDbService,
    ['mongo']: MongoDbService,
    ['derby']: DerbyDbService
}
const DbServiceFactory = (name: string, ...args: any[]): IDbService => {
    if (!(name in DBServices)) {
        throw new Error(`No Db Service for ${name} `);
    }
    return new DBServices[name](...args);
}




const main = async (type: string, fileSource: string, limit: number, batchSize: number) => {
    const logger: Logger = new Logger("Main");
    const dbService: IDbService = DbServiceFactory(type);
    const fileStream: DataFile = new DataFile(fileSource);

    const processor: Processor = new Processor({ batchSize, limit }, fileStream, dbService);

    await processor.init();
    logger.time('Writing')
    const result$ = processor.run();
    const result = await result$.toPromise();
    logger.time('Writing', true)

    logger.time('Destroy');
    const dinitResult = await processor.denit();
    logger.time('Destroy', true)
    console.log("Denit ", dinitResult);



}

interface CLIOptions {
    service: string;
    file: string;
    limit: number;
    batchSize: number;
}

var argv: CLIOptions = yargs.scriptName('dbtester')
    .usage('$0 <cmd> [args]')
    .help('h')
    .alias('h', 'help')
    .command('write', 'runs a test load')
    .alias('s', 'service')
    .describe('s', 'service to use for loading')
    .default('s', 'mock')
    .choices('s', ['mock', 'mongo', 'derby'])
    .nargs('s', 1)
    .alias('f', 'file')
    .describe('f', 'csv file to load the data from')
    .default('f', './data.csv')
    .nargs('f', 1)
    .alias('l', 'limit')
    .describe('l', 'The total number of row you want to run 0 == all')
    .default('l', 0)
    .nargs('l', 1)
    .alias('b', 'batchSize')
    .describe('b', 'The number of items you want to run in each batch 0 == all')
    .default('b', 0)
    .nargs('b', 1)
    .argv as unknown as CLIOptions;



main(argv.service, argv.file, argv.limit, argv.batchSize)




