package com.rodnog.rogermiddenway.restaurantmap;

import com.google.android.gms.maps.model.LatLng;

public class Restaurant {
    private int id;
    private String name;
    private double longitude;
    private  double latitude;

    public Restaurant(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Restaurant() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLongitude() {
        return longitude;
    }
    public LatLng getLatLng(){
        return new LatLng(latitude, longitude);
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }
}
