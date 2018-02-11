package com.fishhackathon.hackathon.fishhackathon.controllers;

public class APIController {
    private static final String BASE_URL = "https://protected-sierra-23327.herokuapp.com";

    private static final String ENDPOINT_NEAR_ZONES = "/nearzones";
    private static final String ENDPOINT_MY_ZONES = "/myzones";

    public static String getURLForNearZones(double latitude, double longitude) {
        return BASE_URL + ENDPOINT_NEAR_ZONES + "?lat=" + latitude + "&lng=" + longitude;
    }

    public static String getURLForMyZones(double latitude, double longitude) {
        return BASE_URL + ENDPOINT_MY_ZONES + "?lat=" + latitude + "&lng=" + longitude;
    }
}