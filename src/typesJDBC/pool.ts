import { Callback, Connection } from "./connection";

export interface Pool {
    status: (callback: Callback) => void
    _addConnectionsOnInitialize: (callback: Callback) => void
    initialize: (callback: Callback) => void
    reserve: (callback: Callback<Connection>) => void
    _closeIdleConnections: () => void
    release: (conn, callback: Callback) => void
    purge: (callback: Callback) => void
}


export interface IJDBC extends Pool { }