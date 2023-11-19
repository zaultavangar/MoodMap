"use strict";
var __createBinding = (this && this.__createBinding) || (Object.create ? (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    var desc = Object.getOwnPropertyDescriptor(m, k);
    if (!desc || ("get" in desc ? !m.__esModule : desc.writable || desc.configurable)) {
      desc = { enumerable: true, get: function() { return m[k]; } };
    }
    Object.defineProperty(o, k2, desc);
}) : (function(o, m, k, k2) {
    if (k2 === undefined) k2 = k;
    o[k2] = m[k];
}));
var __setModuleDefault = (this && this.__setModuleDefault) || (Object.create ? (function(o, v) {
    Object.defineProperty(o, "default", { enumerable: true, value: v });
}) : function(o, v) {
    o["default"] = v;
});
var __importStar = (this && this.__importStar) || function (mod) {
    if (mod && mod.__esModule) return mod;
    var result = {};
    if (mod != null) for (var k in mod) if (k !== "default" && Object.prototype.hasOwnProperty.call(mod, k)) __createBinding(result, mod, k);
    __setModuleDefault(result, mod);
    return result;
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.processGeoJsonData = exports.populateDatabaseWithArticles = void 0;
const axios_1 = __importDefault(require("axios"));
const GeoJsonFeature_1 = require("./models/GeoJsonFeature");
const fs_1 = __importDefault(require("fs"));
const StreamArray_1 = require("stream-json/streamers/StreamArray");
const Pick_1 = __importDefault(require("stream-json/filters/Pick"));
const stream_chain_1 = require("stream-chain");
const turf = __importStar(require("@turf/turf"));
const mongoose_1 = __importDefault(require("mongoose"));
const JsonHandler_1 = require("./JsonHandler/JsonHandler");
const Article_1 = require("./models/Article");
const SentimentGateway_1 = require("./gateways/SentimentGateway");
const DatabaseCrudError_1 = require("./errors/DatabaseCrudError");
const NerGateway_1 = require("./gateways/NerGateway");
const section = 'world';
const API_KEY = 'da553cc9-959c-41ad-a737-db9fecde8185';
const pageSize = 200;
const fields = "trailText,bodyText,thumbnail";
const ADMN_FILE_PATHS = ['./geoJsonData/geoBoundariesCGAZ_ADM0.geojson', './geoJsonData/geoBoundariesCGAZ_ADM1.geojson', './geoJsonData/geoBoundariesCGAZ_ADM2.geojson'];
const getArticlesFromGuardian = (fromDate, toDate) => __awaiter(void 0, void 0, void 0, function* () {
    var _a;
    try {
        const res = yield axios_1.default.get(`https://content.guardianapis.com/search`, {
            params: {
                section: section,
                page: 1,
                'page-size': pageSize,
                'show-fields': fields,
                'from-date': fromDate,
                'to-date': toDate,
                'api-key': API_KEY
            }
        });
        return (_a = res.data) !== null && _a !== void 0 ? _a : null;
    }
    catch (error) {
        if (axios_1.default.isAxiosError(error)) {
            console.error('AxiosError: ', error.status, error.response);
        }
        else {
            console.error('Error: ', error);
        }
        return null;
    }
});
const processArticle = (article) => __awaiter(void 0, void 0, void 0, function* () {
    var _b;
    if (article.type === 'liveblog')
        return null; // Skip liveblog articles
    // Destructure necessary article fields
    const { webPublicationDate, webTitle, webUrl, fields: { trailText, thumbnail, bodyText } } = article;
    console.log('Web title: ', webTitle);
    console.log('Trail text: ', trailText);
    console.log('Body text: ', bodyText);
    // Perform NER and Sentiment Analysis
    const nerInput = webTitle.concat(bodyText, trailText);
    const entities = yield NerGateway_1.NerGateway.getEntities(nerInput);
    const sentimentInput = webTitle.concat(bodyText);
    const sentimentScore = yield SentimentGateway_1.SentimentGateway.getSentimentScore(sentimentInput);
    // Location processing
    let parentIds = [];
    let locationIds = [];
    let featuresList = [];
    let entityToFeaturesMap = {}; // TODO: Make typesafe
    for (const entity of entities) {
        // retrieve or find the matching location features for specified entity
        let matchingLocationFeatures = entityToFeaturesMap[entity.word] || (yield getMatchingLocationFeature(entity));
        if (!(matchingLocationFeatures && matchingLocationFeatures.length > 0)) {
            console.log('No matching location found:', entity.word);
            continue;
        }
        // Cache the found features for future reference
        entityToFeaturesMap[entity.word] = matchingLocationFeatures;
        for (const feature of matchingLocationFeatures) {
            console.log('Feature: ', (_b = feature.properties) === null || _b === void 0 ? void 0 : _b.shapeName);
            const parentFeature = yield findParentFeature(feature);
            if (parentFeature) {
                featuresList.push({
                    self: feature,
                    parent: parentFeature
                });
            }
        }
    }
    const filteredFeatures = filterOutliers(featuresList);
    for (const feature of filteredFeatures) {
        console.log(feature.self._id, feature.parent._id);
        locationIds.push(feature.self._id);
        parentIds.push(feature.parent._id);
    }
    if (parentIds.length > 0 && locationIds.length > 0) {
        yield createArticle(webTitle, thumbnail, webPublicationDate, webUrl, bodyText, sentimentScore, locationIds, parentIds);
        console.log('Article created!');
    }
});
const populateDatabaseWithArticles = (fromDate, toDate) => __awaiter(void 0, void 0, void 0, function* () {
    const guardianRes = yield getArticlesFromGuardian(fromDate, toDate);
    if (guardianRes === null || guardianRes === void 0 ? void 0 : guardianRes.response) {
        const results = guardianRes.response.results;
        let count = 0;
        for (const article of results) {
            try {
                yield processArticle(article);
                console.log(`Article ${count} processed...`);
                count++;
            }
            catch (error) {
                // if (error instanceof DatabaseCrudError)
                console.error(error);
            }
        }
    }
    else {
        console.log('Error in Guardian API response');
    }
});
exports.populateDatabaseWithArticles = populateDatabaseWithArticles;
const createArticle = (webTitle, thumbnail, webPublicationDate, webUrl, bodyText, sentimentScore, locationIds, parentIds) => __awaiter(void 0, void 0, void 0, function* () {
    const newArticle = {
        headline: webTitle,
        thumbnail: thumbnail ? thumbnail : '',
        publicationDate: new Date(webPublicationDate),
        url: webUrl,
        bodyText: `${bodyText.slice(0, 400)}...`,
        sentimentScore: sentimentScore,
        locationIds: locationIds,
        parentLocationIds: parentIds
    };
    yield Article_1.ArticleModel
        .create(newArticle)
        .catch(() => {
        throw new DatabaseCrudError_1.DatabaseCrudError("Error creating article ");
    });
});
function getDistanceBetweenPoints(point1, point2) {
    const from = turf.point(point1);
    const to = turf.point(point2);
    return turf.distance(from, to); // returns distance in kilometers
}
// featuresList.push({
//   self: matchingLocationFeature,
//   parent: matchingParentFeature
// });
function filterOutliers(features) {
    // check if an entity's parent is another entity
    // if so, than we can be pretty sure we're talking about a specific place
    // 1. Calculate the Frequency of each Feature
    const featureFrequencies = {};
    features.forEach(feature => {
        var _a;
        const featureName = (_a = feature.self.properties) === null || _a === void 0 ? void 0 : _a.shapeName;
        if (featureName) {
            featureFrequencies[featureName] = (featureFrequencies[featureName] || 0) + 1;
        }
    });
    const centersOfMass = features.map(feature => {
        const center = turf.centerOfMass(feature.self);
        return {
            originalFeature: feature,
            center: center
        };
    });
    const distances = centersOfMass.map((el1, idx1) => {
        const avgDistanceToOthers = centersOfMass.reduce((sum, el2, idx2) => {
            var _a;
            if (idx1 !== idx2) {
                const distanceBetweenPoints = getDistanceBetweenPoints(el1.center.geometry.coordinates, el2.center.geometry.coordinates);
                const weight = featureFrequencies[((_a = el2.originalFeature.self.properties) === null || _a === void 0 ? void 0 : _a.shapeName) || ""] || 1;
                return sum + (distanceBetweenPoints / weight);
            }
            return sum;
        }, 0) / (centersOfMass.length - 1);
        return {
            feature: el1.originalFeature,
            avgDistance: avgDistanceToOthers
        };
    });
    const overallAvgDistance = distances.reduce((sum, item) => sum + item.avgDistance, 0) / distances.length;
    // 3. Adjust the Outlier Filtering Criterion
    const filteredFeatures = distances.filter(item => {
        if (item.avgDistance < 1.5 * overallAvgDistance) {
            console.log(item.feature.self.properties.shapeName, item.avgDistance, overallAvgDistance);
        }
        item.avgDistance <= 1.5 * overallAvgDistance;
    })
        .map(item => item.feature);
    return filteredFeatures;
}
const findParentFeature = (feature) => __awaiter(void 0, void 0, void 0, function* () {
    var _c;
    const parentGroup = (_c = feature.properties) === null || _c === void 0 ? void 0 : _c.shapeGroup;
    const matchingParentFeature = yield GeoJsonFeature_1.FeatureModel.findOne({
        "properties.shapeGroup": parentGroup,
        "properties.shapeType": {
            $in: ['ADM0', 'DISP']
        }
    }).catch(() => {
        throw new DatabaseCrudError_1.DatabaseCrudError("Error finding parent feature given shapeGroup and shapeType.");
    });
    return isFeature(matchingParentFeature) ? matchingParentFeature : null;
});
const getMatchingLocationFeature = (entity) => __awaiter(void 0, void 0, void 0, function* () {
    if (entity.score > 0.8) {
        const location = entity.word;
        // also search based on group 
        const matchingLocationFeatures = yield GeoJsonFeature_1.FeatureModel.find({
            "properties.shapeName": {
                $eq: location // This will match the value exactly
            } // also check group (location might be abbreviated (e.g. US))
        }).collation({ locale: 'en', strength: 2 })
            .catch(() => {
            throw new DatabaseCrudError_1.DatabaseCrudError("Error finding feature from shapeName.");
        });
        const features = [];
        for (const feature of matchingLocationFeatures) {
            if (isFeature(feature)) {
                features.push(feature);
            }
        }
        return features;
    }
    return null;
});
const isFeature = (feature) => {
    return feature && feature.geometry && feature.properties && feature.properties.shapeGroup && feature.properties.shapeType;
};
const processGeoJsonData = () => __awaiter(void 0, void 0, void 0, function* () {
    console.log('here');
    try {
        yield new Promise((resolve, reject) => {
            for (let idx = 0; idx < ADMN_FILE_PATHS.length; idx++) {
                console.log('File path: ', ADMN_FILE_PATHS[idx]);
                const pipeline = (0, stream_chain_1.chain)([
                    fs_1.default.createReadStream(ADMN_FILE_PATHS[idx]),
                    Pick_1.default.withParser({ filter: 'features' }),
                    (0, StreamArray_1.streamArray)()
                ]);
                const features = [];
                let count = 0;
                pipeline.on('data', (data) => __awaiter(void 0, void 0, void 0, function* () {
                    const feature = data.value;
                    features.push(feature);
                    // console.log('Feature pushed', new Date())
                    count += 1;
                    if (features.length === 50) {
                        pipeline.pause();
                        // let newFeatures: GeoJSON.Feature<GeoJSON.Geometry, GeoJSON.GeoJsonProperties>[] = []
                        // for (const feature of features){
                        //   const d: any = feature.geometry.coordinates[0];
                        //   console.log('Coordinates')
                        //   const cleanedFeature = turf.cleanCoords(feature, {
                        //       mutate: true
                        //   });
                        //   newFeatures.push(cleanedFeature);
                        // }
                        const featureCollection = JsonHandler_1.GeoJsonHandler.createFeatureCollection(features);
                        const simplifiedGeoJson = turf.simplify(featureCollection, { tolerance: 0.01, highQuality: true });
                        try {
                            // check for errors in inserting many 
                            yield GeoJsonFeature_1.FeatureModel.insertMany(simplifiedGeoJson.features, { ordered: false });
                        }
                        catch (error) {
                            console.log('Error: ', error);
                        }
                        console.log(`10 features inserted; insertion ${count}, ${new Date()}`);
                        features.length = 0;
                        pipeline.resume();
                    }
                }));
                pipeline.on('end', () => __awaiter(void 0, void 0, void 0, function* () {
                    if (features.length > 0) {
                        try {
                            yield GeoJsonFeature_1.FeatureModel.insertMany(features, { ordered: false });
                        }
                        catch (error) {
                            console.error('Error: ', error);
                        }
                    }
                    console.log('Finished: ', ADMN_FILE_PATHS[idx]);
                    if (idx === 3) {
                        resolve(); // resolve when pipeline ends
                    }
                }));
                pipeline.on('error', reject); // reject on error
            }
        });
    }
    catch (error) {
        console.log("Error:", error);
    }
    finally {
        console.log('closing');
        mongoose_1.default.connection.close();
    }
});
exports.processGeoJsonData = processGeoJsonData;
