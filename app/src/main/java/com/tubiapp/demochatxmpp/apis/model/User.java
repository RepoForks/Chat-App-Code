package com.tubiapp.demochatxmpp.apis.model;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 5/28/15.
 */
public class User {
    private String email;
    private String password;
    private boolean right_left_show_msg;
    //private String time;

    public User(String email, String password, boolean right_left_show) {
        this.email = email;
        this.password = password;
        right_left_show_msg = right_left_show;
//        time = timestamp;
    }

    /*public String getTimestamp() {
        return time;
    }*/

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return email.split("@")[0];
    }

    public boolean getright_left_msg() {
        return right_left_show_msg;
    }

}
