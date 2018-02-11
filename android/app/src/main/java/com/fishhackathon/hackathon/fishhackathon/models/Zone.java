package com.fishhackathon.hackathon.fishhackathon.models;

import java.util.ArrayList;

public class Zone {
    private String id;
    private String code;
    private String laws;
    private String level;
    private String ocean;
    private ArrayList<MapPolygon> polygonsArrayList;

    public Zone(String id, String code, String laws, String level, String ocean, ArrayList<MapPolygon> polygonsArrayList) {
        this.id = id;
        this.code = code;
        this.laws = laws;
        this.level = level;
        this.ocean = ocean;
        this.polygonsArrayList = polygonsArrayList;
    }

    public String getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public ArrayList<MapPolygon> getPolygons() {
        return polygonsArrayList;
    }

    public String getLaws() {
        return laws;
    }

    public String getLevel() {
        return level;
    }

    public String getOcean() {
        return ocean;
    }
}
