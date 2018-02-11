package com.fishhackathon.hackathon.fishhackathon.models;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class MapPolygon {
    private ArrayList<MapGeoPoint> pointsArrayList;

    public MapPolygon(@NonNull ArrayList<MapGeoPoint> points) {
        this.pointsArrayList = points;
    }

    public ArrayList<MapGeoPoint> getPointsArrayList() {
        return pointsArrayList;
    }
}
