import express, {Express} from 'express';
import axios from 'axios';
import cors from 'cors';
import dotenv from 'dotenv';
import bodyParser from 'body-parser';
import mongoose from 'mongoose';
import { ApiRouter } from './routes/api/ApiRouter';

dotenv.config();

const PORT = process.env.PORT;
const URI = process.env.MONGODB_URI as string;

const app: Express = express();

app.listen(PORT, () => {
  console.log(`Backend server is running on port ${PORT}`);
})

app.use(cors());
app.use(bodyParser.urlencoded({extended: true})); // recognize incoming request object as strings or arrays
app.use(bodyParser.json({limit: '10mb'})) // recognize incoming request object as a JSON object

// connect to DB
mongoose.connect(URI)
  .then(() => {
    console.log('DB connection sucecssful')
    })
  .catch((err) => console.log(err))

const apiRouter = new ApiRouter();
app.use("/api", apiRouter.getExpressRouter());
