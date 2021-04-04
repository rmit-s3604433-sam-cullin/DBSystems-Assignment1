import { DataFile } from "./datafile";
import { MockDbService } from "./services/mock";
import { MongoDbService, MongoServiceConfig } from "./services/mongo";
import { Processor } from "./processor";
import { IDbService, Type } from "./types";
import { DerbyDbService, DerbyServiceConfig } from "./services/derby";
import * as dotenv from 'dotenv'
import * as yargs from 'yargs';
import { alias } from "yargs";
import { Logger } from "./services/logger";
dotenv.config();



const DBServices: { [key: string]: (options: CLIOptions) => IDbService } = {
    ['mock']: (options: CLIOptions) => {
        return new MockDbService()
    },
    ['mongo']: (options: CLIOptions) => {
        return new MongoDbService(
            new MongoServiceConfig(options.optimize == true)
        )
    },
    ['derby']: (options: CLIOptions) => {
        return new DerbyDbService(
            new DerbyServiceConfig(options.optimize == true)
        )
    },
}
const DbServiceFactory = (options: CLIOptions): IDbService => {
    if (!(options.service in DBServices)) {
        throw new Error(`No Db Service for ${options.service} `);
    }
    return DBServices[options.service](options);
}




const main = async (commands: string[], options: CLIOptions) => {
    let { file, query, limit, batchSize } = options;

    const logger: Logger = new Logger("Main");
    logger.time("Total");



    const dbService: IDbService = DbServiceFactory(options);
    const fileStream: DataFile = new DataFile(file);
    const processor: Processor = new Processor({ batchSize, limit }, fileStream, dbService);


    logger.time("Init");
    await processor.init();
    logger.time("Init", true)

    if (commands.includes("write")) {
        logger.time('Writing')
        const result$ = processor.save();
        const result = await result$.toPromise()
        logger.time('Writing', true)
    }

    if (commands.includes("query")) {
        logger.time('Query');
        const result = await dbService.queryId(query);
        logger.info("Result", result)
        logger.time('Query', true)
    }

    if (commands.includes("clean")) {
        logger.time('Clean');
        const result = await dbService.clean();
        logger.info("Result", result);
        logger.time('Clean', true)
    }


    logger.time('Deinit');
    const dinitResult = await processor.denit();
    logger.time('Deinit', true)

    logger.time("Total", true)


    logger.times();



}


interface CLIOptions {
    service: string;
    file: string;
    limit: number;
    batchSize: number;
    query: string;
    command: string;
    optimize: boolean;
}



var argBuilder = yargs.scriptName('dbtester')
    .usage('$0 <cmd> [args]')
    .help('h')
    .alias('h', 'help')
    .command('write', 'runs loads a CSV file into the database')
    .command('clean', 'cleans all data from the database')
    .command('query', 'queries the database for an id')
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
    .alias('q', 'query')
    .describe('q', 'The id to query in the database')
    .default('q', 23413)
    .nargs('q', 1)
    .alias('o', 'optimize')
    .describe('o', 'Weather or not to optimize the Db Service')
    .default('o', false)
    .boolean('o')

var argv: CLIOptions = argBuilder.argv as unknown as CLIOptions;
var commands = argv["_"];


main(commands, argv)




