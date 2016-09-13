package com.tubiapp.demochatxmpp.apis.model;

import com.google.gson.JsonObject;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 5/28/15.
 */
public class MyResponse {
    private String success;
    private Integer status_code;
    private JsonObject data;

    @Override
    public String toString() {
        return data.toString();
    }
}
