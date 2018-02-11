package com.fishhackathon.hackathon.fishhackathon.models;

import java.io.Serializable;

public class Law implements Serializable{
    private String name;
    private String urlInfo;

    public Law(String name, String urlInfo) {
        this.name = name;
        this.urlInfo = urlInfo;
    }

    public String getName() {
        return name;
    }

    public String getUrlInfo() {
        return urlInfo;
    }
}
