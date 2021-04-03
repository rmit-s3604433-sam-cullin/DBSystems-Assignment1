import { ENETDOWN } from "node:constants";


export interface Type<T = any> extends Function {
    new(...args: any[]): T;
}

export class Entity {
    id: number;
    dateTime: string;
    year: number;
    mDate: number;
    month: string;
    day: string;
    time: number;
    sensorId: number;
    sensorName: string;
    hourlyCount: number;

    static create(row: any[]) {
        let entity = new Entity();
        entity.id = Number(row[0]); // ID,
        entity.dateTime = row[1]; // Date_Time,
        entity.year = Number(row[2]); // Year,
        entity.month = row[3]; // Month,
        entity.mDate = Number(row[4]); // Mdate,
        entity.day = row[5]; // Day,
        entity.time = Number(row[6]); // Time,
        entity.sensorId = Number(row[7]); // Sensor_Id,
        entity.sensorName = row[8]; // Sensor_Name,
        entity.hourlyCount = Number(row[9]); // Hourly_Counts
        return entity;
    }
}

export interface InitResult {
    success: boolean
    error?: any
}
export interface IDbService {
    init(): Promise<InitResult>;
    save(collection: string, data: Entity[]): Promise<any>;
    denit(): Promise<any>;
}