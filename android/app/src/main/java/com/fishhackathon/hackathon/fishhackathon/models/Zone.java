package com.fishhackathon.hackathon.fishhackathon.models;

import java.io.Serializable;
import java.util.ArrayList;

public class Zone implements Serializable {
    private String id;
    private String code;
    private ArrayList<Law> laws;
    private String level;
    private String ocean;
    private ArrayList<MapPolygon> polygonsArrayList;

    public Zone(String id, String code, ArrayList<Law> laws, String level, String ocean, ArrayList<MapPolygon> polygonsArrayList) {
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

    public ArrayList<Law> getLaws() {
        return laws;
    }

    public String getLevel() {
        return level;
    }

    public String getOcean() {
        return ocean;
    }
}
