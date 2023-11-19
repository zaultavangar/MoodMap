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
exports.handleNERRequest = void 0;
const handleNERRequest = (req, res) => __awaiter(void 0, void 0, void 0, function* () {
    try {
        const text = req.body.text;
        const data = { "inputs": text };
        const bertNERRes = yield nerQuery(data);
        const locationEntities = [];
        for (const entitity of bertNERRes) { // get location entities
            if (entitity.entity_group === 'LOC')
                locationEntities.push(entitity);
        }
        const locationEntitiesResponse = {
            "entities": locationEntities
        };
        res.json(locationEntitiesResponse);
    }
    catch (error) {
        res.status(500).send(error.message);
    }
});
exports.handleNERRequest = handleNERRequest;
const nerQuery = (data) => __awaiter(void 0, void 0, void 0, function* () {
    const response = yield fetch("https://api-inference.huggingface.co/models/dslim/bert-base-NER", {
        headers: { Authorization: "Bearer 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" },
        method: "POST",
        body: JSON.stringify(data),
    });
    const result = yield response.json();
    return result;
});
