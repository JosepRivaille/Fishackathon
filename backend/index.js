import express from 'express';
import bodyParser from 'body-parser';
import mongoose from 'mongoose';
import {graphqlExpress, graphiqlExpress} from 'apollo-server-express';

import schema from './src';
import {getFilteredZones} from './src/zones/script_zones';

const app = express();
const mongoURL = 'mongodb://FrancescFisher:The_fisher64@thefisherpalace-shard-00-00-qtem3.mongodb.net:27017,' +
    'thefisherpalace-shard-00-01-qtem3.mongodb.net:27017,thefisherpalace-shard-00-02-qtem3.mongodb.net:27017' +
    '/test?ssl=true&replicaSet=TheFisherPalace-shard-0&authSource=admin';
const portToListen = process.env.PORT || 3000;
app.listen(portToListen, () => {
    console.log('Listening on port ' + portToListen);
});

// MiddleWares

app.use(bodyParser.urlencoded({
    extended: true
}));

mongoose.connect(mongoURL)
    .then(() => console.log('Connecting to MongoDB'))
    .catch((err) => console.error(err));

mongoose.connection.once('open', () => {
    console.log('Connection to DB was successful!');
});

app.use('/graphiql', graphiqlExpress({
    endpointURL: '/graphql'
}));

app.use('/graphql', bodyParser.json(), graphqlExpress({schema}));

app.get('/myzones', (req, res) => {
    var lat = req.query.lat;
    var lng = req.query.lng;

    var zoneUtils = require('./src/zones/script_zones');

    //zoneUtils.deleteZones();
    //zoneUtils.insertZones();

    zoneUtils.insideZones(res, lat, lng);
});

app.get('/nearzones', (req, res) => {
    const {lat, lng, currentDay, shipSize, professional, kind} = req.query;

    const coords = {lat, lng};
    const today = new Date(currentDay);
    const attributes = {
        shipSize, professional, kind
    };

    getFilteredZones(res, coords, today, attributes);
});