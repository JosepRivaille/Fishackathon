package com.fishhackathon.hackathon.fishhackathon.models;

import android.location.Location;

public class ARPoint {
    private Location location;
    private String name;

    public ARPoint(String name, double lat, double lon, double altitude) {
        this.name = name;
        this.location = new Location("ARPoint");
        this.location.setLatitude(lat);
        this.location.setLongitude(lon);
        this.location.setAltitude(altitude);
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return this.name;
    }
}
