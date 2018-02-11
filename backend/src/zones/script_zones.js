import ZoneModel from './model';
import inside from 'point-in-polygon';
import classifyPoint from 'robust-point-in-polygon';

module.exports = 
{
  deleteZones: function () {deleteZones()},
  insertZones: function () {insertZones()},
  nearZones: function (res, lat, lng) {nearZones(res, lat, lng)},
  insideZones: function (res, lat, lng) {insideZones(res, lat, lng)},
  getDistanceFromLatLonInKm: function (lat1,lon1,lat2,lon2) {getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2)}
};

function deleteZones() {
	ZoneModel.deleteMany({}, function (err, r) {
	    if (err) {
	      console.log(err)
	    } else {
	      console.log("OK")
	    }
	  });
}

function insertZones() {
	var fs = require('fs');
	var json = JSON.parse(fs.readFileSync('src/zones/FAO_AREAS/FAO_AREAS.json', 'utf8'));
	var features = json["features"];

	for(var i = 0; i < features.length; i++) {
		var properties = features[i]["properties"];
		var code = properties["F_CODE"];
		var level = properties["F_LEVEL"];
		var ocean = properties["OCEAN"].toUpperCase();
		if (!["ATLANTIC", "INDIAN", "PACIFIC", "ARTIC"].includes(ocean)) ocean = "ATLANTIC";
		var parent = "";
		if (level === "SUBAREA") parent = properties["F_AREA"];
		else if (level === "DIVISION") parent = properties["F_SUBAREA"];
		else if (level === "SUBDIVISION") parent = properties["F_DIVISION"];
		else if (level === "SUBUNIT") parent = properties["F_SUBDIVIS"];

		var geometry = features[i]["geometry"];
		var coordinates = [];
		var centroids = [];

		//console.log(geometry.coordinates)
		for (var p = 0; p < geometry.coordinates.length; ++p) {
			var polygon = geometry.coordinates[p];
			var firstring = polygon[0];
			var polygoncoords = []
			firstring.forEach(function(point){
				var latlong = {
			   		lat: point[1],
	        		lng: point[0]
	    		}
			   	polygoncoords.push(latlong);
			});
			var centerpolygon = computeCenterOfPolygon(polygoncoords);
			centroids.push(centerpolygon);

			coordinates.push(polygoncoords);
		}

		var centroidLat = centroids.reduce((a, o, i, p) => a + o.lat / p.length, 0);
		var centroidLng = centroids.reduce((a, o, i, p) => a + o.lng / p.length, 0);
		var centroid = {lat: centroidLat, lng: centroidLng};

		if (coordinates.length > 0) createZone(code, level, ocean, parent, coordinates, centroid);
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

function insideZones(res, lat, lng) {
	var coordinates = [lat, lng];

	ZoneModel.find({}, function (err, zones) {
	    if (err) {
	     	console.log(err);
	      	res.send("ERROR");
	    } else {
  			var zonesInside = [];
			zones.forEach(function(zone){
				zone.polygon.forEach(function(polygon){
					var polygonArray = polygon.map( Object.values );

					//var isInside = inside(coordinates, polygonArray);
					var isInside = classifyPoint(polygonArray, coordinates);
					if (isInside) zonesInside.push(zone);
				})
			})

			var numCharsSmallestZoneCode = 0;
  			var smallestZoneInside = {};
			zonesInside.forEach(function(zone){
				if (numCharsSmallestZoneCode < zone.code.length) {
					numCharsSmallestZoneCode = zone.code.length;
					smallestZoneInside = zone;
				}
			})

			res.send(smallestZoneInside);
	    }
  	});	
}

function nearZones(res, lat, lng) {
	var maxDistance = 6000;

	ZoneModel.find({}, function (err, zones) {
	    if (err) {
	     	console.log(err);
	      	res.send("ERROR");
	    } else {
  			var result = [];

			zones.forEach(function(zone){
				var zoneIsNear = false;
				var i = 0;
				while (!zoneIsNear && i < zone.polygon.length) {
					var j = 0;
					while (!zoneIsNear && j < zone.polygon[i].length) {
						var point = zone.polygon[i][j];
						var distFromZone = getDistanceFromLatLonInKm(lat, lng, point.lat, point.lng);
						if (distFromZone < maxDistance) {
							zoneIsNear = true;
							result.push(zone);
						}
						++j;
					}
					++i;
				}
			})
			res.send(result);
	    }
  	});	
}

function getDistanceFromLatLonInKm(lat1,lon1,lat2,lon2) {
  var R = 6371; // Radius of the earth in km
  var dLat = deg2rad(lat2-lat1);  // deg2rad below
  var dLon = deg2rad(lon2-lon1); 
  var a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
    ; 
  var c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
  var d = R * c; // Distance in km
  return d;
}

function deg2rad(deg) {
  return deg * (Math.PI/180)
}

