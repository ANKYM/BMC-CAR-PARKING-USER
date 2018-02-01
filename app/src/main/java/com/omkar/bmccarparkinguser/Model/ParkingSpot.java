package com.omkar.bmccarparkinguser.Model;

/**
 * Created by omkar on 31-Jan-18.
 */

public class ParkingSpot {
    private String spotID;
    private String spotName;
    private String address;
    private String lat;
    private String longi;
    private int parkCapicity;
    private int parkVehicle;

    public ParkingSpot(String spotID, String spotName, String address, String lat, String longi, int parkCapicity, int parkVehicle) {
        this.spotID = spotID;
        this.spotName = spotName;
        this.address = address;
        this.lat = lat;
        this.longi = longi;
        this.parkCapicity = parkCapicity;
        this.parkVehicle = parkVehicle;
    }

    public ParkingSpot() {
    }

    public String getSpotID() {
        return spotID;
    }

    public void setSpotID(String spotID) {
        this.spotID = spotID;
    }

    public String getSpotName() {
        return spotName;
    }

    public void setSpotName(String spotName) {
        this.spotName = spotName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLongi() {
        return longi;
    }

    public void setLongi(String longi) {
        this.longi = longi;
    }

    public int getParkCapicity() {
        return parkCapicity;
    }

    public void setParkCapicity(int parkCapicity) {
        this.parkCapicity = parkCapicity;
    }

    public int getParkVehicle() {
        return parkVehicle;
    }

    public void setParkVehicle(int parkVehicle) {
        this.parkVehicle = parkVehicle;
    }
}
