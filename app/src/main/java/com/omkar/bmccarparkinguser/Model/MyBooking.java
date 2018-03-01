package com.omkar.bmccarparkinguser.Model;

import java.io.Serializable;

/**
 * Created by omkar on 27-Feb-18.
 */

public class MyBooking implements Serializable {
    String vehicleNo;
    String lotId;
    String ownerId;
    String ownerMobileNo;
    String vehicleType;
    String bookingTime;
    String bookingDuration;
    Integer bookConfrimed;
    String bookingToken;

    public MyBooking(String vehicleNo, String lotId, String ownerId, String ownerMobileNo, String vehicleType, String bookingTime, String bookingDuration, Integer bookConfrimed, String bookingToken) {
        this.vehicleNo = vehicleNo;
        this.lotId = lotId;
        this.ownerId = ownerId;
        this.ownerMobileNo = ownerMobileNo;
        this.vehicleType = vehicleType;
        this.bookingTime = bookingTime;
        this.bookingDuration = bookingDuration;
        this.bookConfrimed = bookConfrimed;
        this.bookingToken = bookingToken;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerMobileNo() {
        return ownerMobileNo;
    }

    public void setOwnerMobileNo(String ownerMobileNo) {
        this.ownerMobileNo = ownerMobileNo;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(String bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getBookingDuration() {
        return bookingDuration;
    }

    public void setBookingDuration(String bookingDuration) {
        this.bookingDuration = bookingDuration;
    }

    public Integer getBookConfrimed() {
        return bookConfrimed;
    }

    public void setBookConfrimed(Integer bookConfrimed) {
        this.bookConfrimed = bookConfrimed;
    }

    public String getBookingToken() {
        return bookingToken;
    }

    public void setBookingToken(String bookingToken) {
        this.bookingToken = bookingToken;
    }

    @Override
    public String toString() {
        return "MyBooking{" +
                "vehicleNo='" + vehicleNo + '\'' +
                ", lotId='" + lotId + '\'' +
                ", ownerId='" + ownerId + '\'' +
                ", ownerMobileNo='" + ownerMobileNo + '\'' +
                ", vehicleType='" + vehicleType + '\'' +
                ", bookingTime='" + bookingTime + '\'' +
                ", bookingDuration='" + bookingDuration + '\'' +
                ", bookConfrimed='" + bookConfrimed + '\'' +
                ", bookingToken='" + bookingToken + '\'' +
                '}';
    }
}
