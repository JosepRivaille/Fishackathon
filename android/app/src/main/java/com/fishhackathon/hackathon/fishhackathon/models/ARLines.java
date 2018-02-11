package com.fishhackathon.hackathon.fishhackathon.models;

public class ARLines {
    private ARPoint pointA;
    private ARPoint pointB;
    private boolean drawLabels;
    private boolean drawPoints;

    public ARLines(ARPoint pointA, ARPoint pointB, boolean drawLabels, boolean drawPoints) {
        this.pointA = pointA;
        this.pointB = pointB;
        this.drawLabels = drawLabels;
        this.drawPoints = drawPoints;
    }

    public ARPoint getPointA() {
        return pointA;
    }

    public ARPoint getPointB() {
        return pointB;
    }

    public boolean isDrawLabels() {
        return drawLabels;
    }

    public boolean isDrawPoints() {
        return drawPoints;
    }
}
