


export class Logger {
    constructor(private readonly context: string) { }


    private timers: {
        [key: string]: {
            start: number,
            end?: number
        }
    } = {};

    public time(key: string, finished: boolean = false) {
        let isNewRecord = false;
        if (!(key in this.timers)) {
            isNewRecord = true;
            this.timers[key] = {
                start: +Date.now()
            }
        }
        if (finished) {
            this.timers[key].end = +Date.now();
        }


        if (this.timers[key].end) {
            this.info(`Finished ${key} start:${this.timers[key].start} end:${this.timers[key].end} time:${this.timers[key].end - this.timers[key].start}`)
        } else if (isNewRecord) {
            this.info(`Started ${key} start:${this.timers[key].start}`)
        } else {
            this.info(`Update ${key} start:${this.timers[key].start} duration:${+Date.now() - this.timers[key].start}`)
        }
    }

    public info(...args: any[]) {
        console.log(`[INFO][${this.context}] -> `, ...args)
    }

    public error(message: string, error: Error, ...args: any[]) {
        console.log(`[ERROR][${this.context}] -> `, message, error.name, error.message, error.stack)
    }

    public warn(...args: any[]) {
        console.log(`[WARN][${this.context}] -> `, ...args)
    }


}