package com.example.myapplication.model;

import com.google.gson.annotations.SerializedName;

public class Place {
    @SerializedName("display_name")
    private String displayName;

    @SerializedName("lat")
    private String lat;   // string trong JSON

    @SerializedName("lon")
    private String lon;   // string trong JSON

    public String getDisplayName() { return displayName; }
    public double getLat() {
        try { return Double.parseDouble(lat); } catch (Exception e) { return 0d; }
    }
    public double getLon() {
        try { return Double.parseDouble(lon); } catch (Exception e) { return 0d; }
    }
}
