"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.FeatureModel = void 0;
const mongoose_1 = __importDefault(require("mongoose"));
const Schema = mongoose_1.default.Schema;
const featureSchema = new Schema({
    type: {
        type: String,
        required: true,
        enum: ['Feature'] // GeoJSON Feature objects have a type of "Feature"
    },
    properties: {
        shapeName: String,
        shapeGroup: String,
        shapeType: String
    },
    geometry: {
        type: {
            type: String,
            required: true,
            enum: ['Point', 'LineString', 'Polygon', 'MultiPoint', 'MultiLineString', 'MultiPolygon'] // Possible geometry types in GeoJSON
        },
        coordinates: {
            type: [],
            required: true
        }
    }
});
// no two documents in the collection have the same combination of 'shapeName' and 'shapeGroup'
featureSchema.index({ "properties.shapeName": 1, "properties.shapeGroup": 1 }, { unique: true });
exports.FeatureModel = mongoose_1.default.model('Geo Json Feature', featureSchema);
