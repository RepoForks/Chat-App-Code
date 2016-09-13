package com.tubiapp.demochatxmpp.apis;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;

public class MyErrorHandler implements ErrorHandler {
    @Override
    public Throwable handleError(RetrofitError cause) {
//    Response r = cause.getResponse();
//    String reason = r.getReason();
//    Log.e("foo", reason);
        return cause;
    }
}