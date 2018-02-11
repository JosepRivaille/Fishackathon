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
		var centroids = [];
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

			var centerpolygon = computeCenterOfPolygon(polygoncoords);
			centroids.push(centerpolygon);
		});
		var centroidLat = centroids.reduce((a, o, i, p) => a + o.lat / p.length, 0);
		var centroidLng = centroids.reduce((a, o, i, p) => a + o.lng / p.length, 0);
		var centroid = {lat: centroidLat, lng: centroidLng}
		console.log(centroid);
		console.log(centroids);

		createZone(code, level, ocean, parent, coordinates, centroid);
	}
}

function createZone(code, level, ocean, parent, polygon, centroid) {
	
	const zone = new ZoneModel({
                id: undefined,
                code,
                level,
                ocean: ocean.toUpperCase(),
                parent,
                polygon: polygon,
                centroid,
                laws: []
            });
    zone.save();
}

function computeCenterOfPolygon(polygon) {
	var maxLat = Math.max.apply(Math,polygon.map(function(o){return o.lat;}));
	var minLat = Math.min.apply(Math,polygon.map(function(o){return o.lat;}));
	var maxLng = Math.max.apply(Math,polygon.map(function(o){return o.lng;}));
	var minLng = Math.min.apply(Math,polygon.map(function(o){return o.lng;}));

	var center = {
		lat: minLat+(maxLat-minLat)/2,
		lng: minLng+(maxLng-minLng)/2
	}

	return center;
}
