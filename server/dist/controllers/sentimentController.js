"use strict";
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.handleSentimentRequest = void 0;
const handleSentimentRequest = (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        const text = yield req.body.text;
        const data = { "inputs": text.slice(0, 512) };
        const bertSentimentRes = yield sentimentQuery(data);
        let weightedAvg = 0;
        for (const sentiment of bertSentimentRes[0]) {
            const label = sentiment.label;
            const star = parseInt(label[0]);
            if (!isNaN(star)) {
                weightedAvg += star * sentiment.score; // sum up star * score 
            }
            else {
                res.json("Error: unexpected label returned from bert API");
                return;
            }
        }
        const normalized = (weightedAvg - 1) / 4.0; // normalize between 0 and 1
        res.json({ sentimentScore: normalized });
    }
    catch (error) {
        res.status(500).send(error.message);
    }
});
exports.handleSentimentRequest = handleSentimentRequest;
const sentimentQuery = (data) => __awaiter(void 0, void 0, void 0, function* () {
    const response = yield fetch("https://api-inference.huggingface.co/models/nlptown/bert-base-multilingual-uncased-sentiment", {
        headers: { Authorization: "Bearer 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" },
        method: "POST",
        body: JSON.stringify(data),
    });
    const result = yield response.json();
    return result;
});
