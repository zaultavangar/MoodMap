"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const body_parser_1 = __importDefault(require("body-parser"));
const NerRouter_1 = require("./NerRouter/NerRouter");
const SentimentRouter_1 = require("./SentimentRouter/SentimentRouter");
const PORT = 3000;
const app = (0, express_1.default)();
app.listen(PORT, () => {
    console.log(`Backend server is running on port ${PORT}`);
});
app.use((0, cors_1.default)());
app.use(body_parser_1.default.urlencoded({ extended: true })); // recognize incoming request object as strings or arrays
app.use(body_parser_1.default.json({ limit: '10mb' })); // recognize incoming request object as a JSON object
const nerRouter = new NerRouter_1.NerRouter();
app.use("/ner", nerRouter.getExpressRouter());
const sentimentRouter = new SentimentRouter_1.SentimentRouter();
app.use("/sentiment", sentimentRouter.getExpressRouter());
