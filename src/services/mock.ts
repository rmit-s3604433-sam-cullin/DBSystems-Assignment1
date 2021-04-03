import { IDbService, InitResult } from "../types";
import { Logger } from "./logger";



export class MockDbService implements IDbService {
    private readonly logger: Logger = new Logger(MockDbService.name);

    init(): Promise<InitResult> {
        this.logger.info('Init Called')
        return Promise.resolve({ success: true })
    }
    save(collection: string, data: any[]): Promise<any> {
        return Promise.resolve({
            success: true,
            collection,
            data
        })
    }
    denit(): Promise<any> {
        this.logger.info('Deinit called')
        return Promise.resolve({
            success: true,
            data: {
                mock: true
            }
        })
    }

}