"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.DatabaseCrudError = void 0;
class DatabaseCrudError extends Error {
    constructor(message) {
        super(message);
        this.name = 'CRUDError';
    }
}
exports.DatabaseCrudError = DatabaseCrudError;
