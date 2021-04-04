import ProgressBar from "progress";



export class Logger {
    constructor(private readonly context: string) { }


    private static timers: {
        [key: string]: {
            start: number,
            end?: number,
            time?: number,
        }
    } = {};

    private static progresses: {
        [key: string]: ProgressBar
    } = {};

    public times() {
        this.info(Logger.timers);
    }

    public time(key: string, finished: boolean = false) {
        let isNewRecord = false;
        if (!(key in Logger.timers)) {
            isNewRecord = true;
            Logger.timers[key] = {
                start: +Date.now()
            }
        }
        if (finished) {
            Logger.timers[key].end = +Date.now();
            Logger.timers[key].time = Logger.timers[key].end - Logger.timers[key].start
        }


        if (Logger.timers[key].end) {
            this.info(`Finished ${key} start:${Logger.timers[key].start} end:${Logger.timers[key].end} time:${Logger.timers[key].end - Logger.timers[key].start}`)
        } else if (isNewRecord) {
            this.info(`Started ${key} start:${Logger.timers[key].start}`)
        } else {
            this.info(`Update ${key} start:${Logger.timers[key].start} duration:${+Date.now() - Logger.timers[key].start}`)
        }
    }


    public progress(key: string, tick: number, size: number = 3574594) {
        if (!(key in Logger.progresses)) {
            Logger.progresses[key] = new ProgressBar(`${this.context} Loading [:bar] :current/:total :percent :etas`, {
                total: size
            })
        }
        let bar = Logger.progresses[key];
        bar.tick(tick);
        if (bar.complete) {
            bar.terminate();
            delete Logger.progresses[key];
        }
    }

    public info(...args: any[]) {
        console.log(`[INFO][${this.context}] -> `, ...args)
    }

    public error(message: string, error: Error, ...args: any[]) {
        console.log(`[ERROR][${this.context}] -> `, message, error.name, error.message)
    }

    public warn(...args: any[]) {
        console.log(`[WARN][${this.context}] -> `, ...args)
    }


}