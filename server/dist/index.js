"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
const express_1 = __importDefault(require("express"));
const cors_1 = __importDefault(require("cors"));
const dotenv_1 = __importDefault(require("dotenv"));
const body_parser_1 = __importDefault(require("body-parser"));
const mongoose_1 = __importDefault(require("mongoose"));
const NerRouter_1 = require("./routes/api/NerRouter");
const SentimentRouter_1 = require("./routes/api/SentimentRouter");
const preprocessing_1 = require("./preprocessing");
dotenv_1.default.config();
const PORT = process.env.PORT;
const URI = process.env.MONGODB_URI;
const app = (0, express_1.default)();
app.listen(PORT, () => {
    console.log(`Backend server is running on port ${PORT}`);
});
app.use((0, cors_1.default)());
app.use(body_parser_1.default.urlencoded({ extended: true })); // recognize incoming request object as strings or arrays
app.use(body_parser_1.default.json({ limit: '10mb' })); // recognize incoming request object as a JSON object
// connect to DB
mongoose_1.default.connect(URI)
    .then(() => {
    console.log('DB connection sucecssful');
    (0, preprocessing_1.processGeoJsonData)();
    // populateDatabaseWithArticles("2023-01-01", "2023-11-01")
    // getGeoparseResults('Palestinian Americans sue state department on behalf of relatives stuck in Gaza. Americans were provided flights from Israel after 7 October Hamas attack, but those in besieged Gaza Strip cannot leave. American citizens trapped in the Gaza Strip and their families in the US are lawyering up after weeks of desperate and futile attempts to exit the war zone, which has been under heavy bombardment by Israel since Hamas’s attacks on 7 October. Nearly a dozen lawsuits have been filed or are set to be filed against t.').then(data => {
    //   console.log(data);
})
    .catch((err) => console.log(err));
app.use("/ner", NerRouter_1.nerExpressRouter);
app.use("/sentiment", SentimentRouter_1.sentimentExpressRouter);
