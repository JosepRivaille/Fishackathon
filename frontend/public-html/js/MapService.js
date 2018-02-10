class MapService {
	constructor(id, lat, lng) {
	    this.initLeaflet(id, lat, lng);
	}

	getMap() {
	    return this.map;
	}

	initLeaflet(id, lat, lng) {
	    this.map = L.map(id).setView([lat, lng], 14);

		var tiles = undefined;
		tiles = L.tileLayer('https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/{z}/{x}/{y}.png', {
			attribution: '<a href="http://leafletjs.com" title="A JS library for interactive maps">Leaflet</a> | ' +
			'&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors, ' +
			'&copy; <a href="https://carto.com/attributions">CARTO</a>'
		})
	    tiles.addTo(this.map);
	}

    loadShapefile() {
    	var shapefileLayer = new L.Shapefile("FAO_AREAS/FAO_AREAS.zip", {onEachFeature: function (feature, layer) {
		    /* Add some colors based on shapefile features */
	    }});

		var baseMaps = {
		    //"example title": example_layer
		};

		var overlayMaps = {
		    "FAO AREAS": shapefileLayer
		};

		L.control.layers(baseMaps, overlayMaps).addTo(this.map);
    }
}