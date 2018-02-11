package com.fishhackathon.hackathon.fishhackathon.controllers;

import android.support.annotation.Nullable;

public class APIController {
    private static final String BASE_URL = "https://protected-sierra-23327.herokuapp.com";

    private static final String ENDPOINT_NEAR_ZONES = "/nearzones";
    private static final String ENDPOINT_MY_ZONES = "/myzones";

    public static String getURLForNearZones(double latitude, double longitude, @Nullable String fromDay, @Nullable String endDay) {
        if (fromDay == null) {
            return BASE_URL + ENDPOINT_NEAR_ZONES + "?lat=" + latitude + "&lng=" + longitude;
        }
        return BASE_URL + ENDPOINT_NEAR_ZONES + "?lat=" + latitude + "&lng=" + longitude + "?fromDay=" + fromDay + "?endDay=" + endDay;
    }

    public static String getURLForMyZones(double latitude, double longitude) {
        return BASE_URL + ENDPOINT_MY_ZONES + "?lat=" + latitude + "&lng=" + longitude;
    }
}