"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.GeoJsonHandler = exports.JsonHandler = void 0;
class JsonHandler {
    constructor() {
        this.parse = (jsonString) => {
            const obj = JSON.parse(jsonString);
            return obj;
        };
        this.stringify = (obj) => {
            return JSON.stringify(obj);
        };
    }
}
exports.JsonHandler = JsonHandler;
class GeoJsonHandler extends JsonHandler {
    static createFeatureCollection(features) {
        return {
            type: 'FeatureCollection',
            features: features
        };
    }
}
exports.GeoJsonHandler = GeoJsonHandler;
