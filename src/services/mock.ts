import { Entity, IDbService, InitResult } from "../types";
import { Logger } from "./logger";



export class MockDbService implements IDbService {
    queryId(id: any): Promise<Entity> {
        return Promise.resolve(new Entity())
    }
    clean(): Promise<any> {
        return Promise.resolve({ success: true })
    }
    private readonly logger: Logger = new Logger(MockDbService.name);

    init(): Promise<InitResult> {
        this.logger.info('Init Called')
        return Promise.resolve({ success: true })
    }
    save(data: any[]): Promise<any> {
        return Promise.resolve({
            success: true,
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