import express from 'express';
import bodyParser from 'body-parser';
import mongoose from 'mongoose';
import {graphqlExpress, graphiqlExpress} from 'apollo-server-express';

import schema from './src';

const app = express();
const mongoURL = 'mongodb://FrancescFisher:The_fisher64@thefisherpalace-shard-00-00-qtem3.mongodb.net:27017,' +
    'thefisherpalace-shard-00-01-qtem3.mongodb.net:27017,thefisherpalace-shard-00-02-qtem3.mongodb.net:27017' +
    '/test?ssl=true&replicaSet=TheFisherPalace-shard-0&authSource=admin';

app.listen(3000, () => {
    console.log('Listening on port 3000');
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

app.get('/zones', (req, res) => {
	//insertZones();
	insidePolygon();
	res.send("OK");
});



import inside from 'point-in-polygon';
import ZoneModel from './src/zones/model';

function insidePolygon() {//lat, lng) {
	//var coordinates = [lat, lng];

	var result = ZoneModel.findOne( { 'code': '21' } );

	/*
	var zones = [];
	var zoneinsideId = -1;
	zones.forEach(function(zone){
		zone.polygon.forEach(function(polygon){
			var isInside = inside(coordinates, polygon);
			if (isInside) zoneinsideId = zone.code;
		})
	})
	*/

	return result;
}


