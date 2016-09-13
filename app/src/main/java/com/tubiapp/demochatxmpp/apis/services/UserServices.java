package com.tubiapp.demochatxmpp.apis.services;

import com.google.gson.JsonObject;
import com.tubiapp.demochatxmpp.apis.model.MyResponse;
import com.tubiapp.demochatxmpp.apis.model.User;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 5/28/15.
 */
public interface UserServices {
    @POST("/v1/login.json")
    void login(@Body User user, Callback<MyResponse> cb);

    @POST("/v1/login.json")
    JsonObject login(@Body User user);
}
