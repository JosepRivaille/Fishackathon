function initializeMapComponent(mapid, mapObj, lat, lng) {
    console.log("initializeMapComponent" + " - " + mapid);
    
    mapObj.mapService = new MapService(mapid, lat, lng);
    mapObj.mapService.loadShapefile();
}