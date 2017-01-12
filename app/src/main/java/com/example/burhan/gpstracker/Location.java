package com.example.burhan.gpstracker;

public class Location {
    private String location;
    private String date;
    private int thumbnail;

    public Location() {
    }

    public Location(String location, String date) {
        this.location = location;
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
