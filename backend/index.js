import express from 'express';
import bodyParser from 'body-parser';
import mongoose from 'mongoose';
import { graphqlExpress, graphiqlExpress } from 'apollo-server-express';

import combine from './src';
import schema from './src/laws/schema';

const app = express();

app.listen(3000, () => {
    console.log('Listening on port 3000');
});

// MiddleWares

app.use(bodyParser.urlencoded({
    extended: true
}));

mongoose.connect('mongodb://127.0.0.1:27017/Fishackathon')
    .then(() => console.log('Connecting to MongoDB'))
    .catch((err) => console.error(err));

mongoose.connection.once('open', () => {
    console.log('Connection to DB was successful!');
});

app.use('/graphiql', graphiqlExpress({
    endpointURL: '/graphql'
}));

app.use('/graphql', bodyParser.json(), graphqlExpress({schema}));

// Routes

app.get('/', (req, res) => {
    const result = combine(req.body);
    res.send(result);
});
