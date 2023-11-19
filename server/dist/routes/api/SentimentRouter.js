"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.sentimentExpressRouter = void 0;
const express_1 = __importDefault(require("express"));
const sentimentController_1 = require("../../controllers/sentimentController");
exports.sentimentExpressRouter = express_1.default.Router();
exports.sentimentExpressRouter.post('/', sentimentController_1.handleSentimentRequest);
