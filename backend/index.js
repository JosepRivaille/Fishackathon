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
	insert_zones();
	res.send("OK");
});

import ZoneModel from './src/zones/model';

function insert_zones() {
	var fs = require('fs');
	var json = JSON.parse(fs.readFileSync('src/zones/FAO_AREAS/FAO_AREAS.json', 'utf8'));
	var features = json["features"];

	for(var i = 0; i < features.length; i++) {
		var properties = features[i]["properties"];
		var code = properties["F_CODE"];
		var level = properties["F_LEVEL"];
		var ocean = properties["OCEAN"];
		var parent = "";
		if (level === "SUBAREA") parent = properties["F_AREA"];
		else if (level === "DIVISION") parent = properties["F_SUBAREA"];
		else if (level === "SUBDIVISION") parent = properties["F_DIVISION"];
		else if (level === "SUBUNIT") parent = properties["F_SUBDIVIS"];

		var geometry = features[i]["geometry"];
		var coordinates = [];
		geometry.coordinates.forEach(function(polygon){
			var polygoncoords = [];
		   	polygon.forEach(function(linearring){
				var firstring = linearring[0];
				var latlong = {
			   		lat: firstring[0],
	        		lng: firstring[1]
	    		}
			   	polygoncoords.push(latlong);
			});
			coordinates.push(polygoncoords);
		});

		create_zone(code, level, ocean, parent, coordinates);
	}
}

function create_zone(code, level, ocean, parent, polygon) {
	
	const zone = new ZoneModel({
                id: undefined,
                code,
                level,
                ocean: ocean.toUpperCase(),
                parent,
                polygon: polygon,
                laws: []
            });
    zone.save();
}


