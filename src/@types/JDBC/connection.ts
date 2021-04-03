export type Callback<Y = any> = (err: any, result: Y) => any

export interface Statement {
    // addBatch: (sql, callback: Callback) => void
    cancel: (callback: Callback) => void
    clearBatch: (callback: Callback) => void
    close: (callback: Callback) => void
    executeUpdate: (sql, callback: Callback) => void
    executeQuery: (sql, callback: Callback) => void
    execute: (sql, callback: Callback) => void
    getFetchSize: (callback: Callback) => void
    setFetchSize: (rows, callback: Callback) => void
    getMaxRows: (callback: Callback) => void
    setMaxRows: (max, callback: Callback) => void
    getQueryTimeout: (callback: Callback) => void
    setQueryTimeout: (seconds, callback: Callback) => void
    getGeneratedKeys: (callback: Callback) => void
}

export interface Connection {
    uuid: string;


    clearWarnings: (callback: Callback) => void;
    close: (callback: Callback) => void;
    commit: (callback: Callback) => void;
    createStatement: (callback: Callback<Statement>) => void;
    createStruct: (typename, attrarr, callback: Callback) => void;
    getAutoCommit: (callback: Callback) => void;
    getCatalog: (callback: Callback) => void;
    getClientInfo: (name, callback: Callback) => void;
    getHoldability: (callback: Callback) => void;
    getMetaData: (callback: Callback) => void;
    getNetworkTimeout: (callback: Callback) => void;
    getSchema: (callback: Callback) => void;
    getTransactionIsolation: (callback: Callback) => void;
    getTypeMap: (callback: Callback) => void;
    getWarnings: (callback: Callback) => void;
    isClosed: (callback: Callback) => void;
    isClosedSync: () => void;
    isReadOnly: (callback: Callback) => void;
    isReadOnlySync: () => void;
    isValid: (timeout: number, callback: Callback) => void;
    isValidSync: (timeout: number) => void;
    nativeSQL: (sql: string, callback: Callback) => void;
    prepareCall: (sql: string, rstype, rsconcurrency, rsholdability, callback: Callback) => void;
    prepareStatement: (sql: string, arg1, arg2, arg3, callback: Callback) => void;
    releaseSavepoint: (savepoint, callback: Callback) => void;
    rollback: (savepoint, callback: Callback) => void;
    setAutoCommit: (autocommit, callback: Callback) => void;
    setCatalog: (catalog, callback: Callback) => void;
    setClientInfo: (props, name, value, callback: Callback) => void;
    setHoldability: (holdability, callback: Callback) => void;
    setNetworkTimeout: (executor, ms, callback: Callback) => void;
    setReadOnly: (readonly, callback: Callback) => void;
    setSavepoint: (name, callback: Callback) => void;
    setSchema: (schema, callback: Callback) => void;
    setTransactionIsolation: (txniso, callback: Callback) => void;
}