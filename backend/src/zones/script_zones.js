import ZoneModel from './src/zones/model';

function insertZones() {
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

		createZone(code, level, ocean, parent, coordinates);
	}
}

function createZone(code, level, ocean, parent, polygon) {
	
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