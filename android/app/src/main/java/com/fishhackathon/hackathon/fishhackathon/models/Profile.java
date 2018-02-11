package com.fishhackathon.hackathon.fishhackathon.models;

import android.content.Context;

import com.fishhackathon.hackathon.fishhackathon.controllers.SharedPreferencesController;

import java.util.ArrayList;

public class Profile {
    private final static String KEY_SIZE_OF_SHIP = "SIZE_OF_SHIP";
    private final static String KEY_TYPE_OF_SHIP = "TYPE_OF_SHIP";
    private final static String KEY_PROFESSIONAL_SHIP = "PROFESSIONAL_SHIP";
    public final static String[] SIZE_OF_SHIP = new String[]{
            "< 10m",
            "> 10m, < 12m",
            "> 12m"};
    public final static String[] TYPE_OF_SHIP = new String[]{
            "PROFESSIONAL",
            "RECREATIONAL",
            "DRAGGER",
            "FENCE",
            "NET",
            "FLY",
            "TRAP",
            "CORAL",
            "HOOK"};
    private String sizeOfShip;
    private String typeOfShip;
    private boolean professionalShip;
    private ArrayList<Species> speciesArrayList;

    public Profile(String sizeOfShip, String typeOfShip, boolean professionalShip, ArrayList<Species> speciesArrayList) {
        this.sizeOfShip = sizeOfShip;
        this.typeOfShip = typeOfShip;
        this.professionalShip = professionalShip;
        this.speciesArrayList = speciesArrayList;
    }

    public Profile(Context context) {
        this.sizeOfShip = SharedPreferencesController.getStringValue(context, KEY_SIZE_OF_SHIP, SIZE_OF_SHIP[0]);
        this.typeOfShip = SharedPreferencesController.getStringValue(context, KEY_TYPE_OF_SHIP, TYPE_OF_SHIP[0]);
        this.professionalShip = SharedPreferencesController.getBooleanValue(context, KEY_PROFESSIONAL_SHIP, false);
    }

    public String getSizeOfShip() {
        return sizeOfShip;
    }

    public void setSizeOfShip(String sizeOfShip) {
        this.sizeOfShip = sizeOfShip;
    }

    public String getTypeOfShip() {
        return typeOfShip;
    }

    public void setTypeOfShip(String typeOfShip) {
        this.typeOfShip = typeOfShip;
    }

    public boolean isProfessionalShip() {
        return professionalShip;
    }

    public void setProfessionalShip(boolean professionalShip) {
        this.professionalShip = professionalShip;
    }

    public ArrayList<Species> getSpeciesArrayList() {
        return speciesArrayList;
    }

    public void setSpeciesArrayList(ArrayList<Species> speciesArrayList) {
        this.speciesArrayList = speciesArrayList;
    }

    public void saveSizeToDB(Context context) {
        SharedPreferencesController.setStringValue(context, KEY_SIZE_OF_SHIP, sizeOfShip);
    }

    public void saveTypeToDB(Context context) {
        SharedPreferencesController.setStringValue(context, KEY_TYPE_OF_SHIP, typeOfShip);
    }

    public void saveProfessionalToDB(Context context) {
        SharedPreferencesController.setBooleanValue(context, KEY_PROFESSIONAL_SHIP, professionalShip);
    }

    public int returnPositionFromRaw(String[] list, String raw) {
        for (int i = 0; i < list.length; ++i) {
            if (list[i].equals(raw)) {
                return i;
            }
        }
        return -1;
    }
}
