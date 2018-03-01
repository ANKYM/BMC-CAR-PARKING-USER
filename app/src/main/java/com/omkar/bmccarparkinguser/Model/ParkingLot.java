package com.omkar.bmccarparkinguser.Model;


import java.io.Serializable;

/**
 * Created by omkar on 31-Jan-18.
 */


public class ParkingLot implements Serializable {

    private String lotid;
    private String lotname;
    private String address;
    private String latitude;
    private String longitude;
    private Integer parkedcapacity;
    private Integer parkedvehicle;
    private Integer distance;

    public ParkingLot(String lotid, String lotname, String address, String latitude, String longitude, Integer parkedcapacity, Integer parkedvehicle,Integer distance) {
        this.lotid = lotid;
        this.lotname = lotname;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parkedcapacity = parkedcapacity;
        this.parkedvehicle = parkedvehicle;
        this.distance = distance;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
        this.distance = distance;
    }

    public String getLotid() {
        return lotid;
    }

    public void setLotid(String lotid) {
        this.lotid = lotid;
    }

    public String getLotname() {
        return lotname;
    }

    public void setLotname(String lotname) {
        this.lotname = lotname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Integer getParkedcapacity() {
        return parkedcapacity;
    }

    public void setParkedcapacity(Integer parkedcapacity) {
        this.parkedcapacity = parkedcapacity;
    }

    public Integer getParkedvehicle() {
        return parkedvehicle;
    }

    public void setParkedvehicle(Integer parkedvehicle) {
        this.parkedvehicle = parkedvehicle;
    }

}



