package com.tubiapp.demochatxmpp.apis;

import com.tubiapp.demochatxmpp.apis.model.MyResponse;
import com.tubiapp.demochatxmpp.apis.model.User;
import com.tubiapp.demochatxmpp.apis.services.UserServices;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright © 2015 AsianTech inc.
 * Created by Justin on 5/28/15.
 */
public class Api {
    private static Api instance;
    private final String END_POINT = "http://10.0.3.2:3000";
    private final RestAdapter restAdapter = new RestAdapter.Builder()
            .setEndpoint(END_POINT)
            .setErrorHandler(new MyErrorHandler())
            .build();

    private Api() {

    }

    public static Api getInstance() {
        if (instance == null) {
            synchronized (Api.class) {
                if (instance == null) {
                    instance = new Api();
                }
            }
        }
        return instance;
    }

    public void login(final User user, final APICallback<MyResponse> callback) {
        ExecutorManager.getServiceExecutor().execute(new Runnable() {
            @Override
            public void run() {
                UserServices userServices = restAdapter.create(UserServices.class);
                userServices.login(user, new Callback<MyResponse>() {
                    @Override
                    public void success(MyResponse myResponse, Response response) {
                        // do background task
                        callback.success(myResponse, response);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        callback.failure(error);
                    }
                });
            }
        });
    }

    public interface APICallback<T> {

        void success(T t, Response response);

        void failure(RetrofitError error);
    }
}
