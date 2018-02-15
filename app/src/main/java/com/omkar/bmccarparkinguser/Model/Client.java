package com.omkar.bmccarparkinguser.Model;

/**
 * Created by omkar on 15-Feb-18.
 */

public class Client {
    String Mobile_no;
    String User_id;
    String UserEmail;

    public Client(String mobile_no, String user_id, String userEmail) {
        Mobile_no = mobile_no;
        User_id = user_id;
        UserEmail = userEmail;
    }

    public String getMobile_no() {
        return Mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        Mobile_no = mobile_no;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        User_id = user_id;
    }

    public String getUserEmail() {
        return UserEmail;
    }

    public void setUserEmail(String userEmail) {
        UserEmail = userEmail;
    }
}
