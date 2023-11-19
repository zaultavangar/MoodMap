"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.nerExpressRouter = void 0;
const express_1 = __importDefault(require("express"));
const nerController_1 = require("../../controllers/nerController");
exports.nerExpressRouter = express_1.default.Router();
exports.nerExpressRouter.post('/', nerController_1.handleNERRequest);
