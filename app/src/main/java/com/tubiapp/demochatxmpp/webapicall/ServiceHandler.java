package com.tubiapp.demochatxmpp.webapicall;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class ServiceHandler {

    public final static int GET = 1;
    public final static int POST = 2;
    static String response = null;

    public ServiceHandler() {

    }

    public String makeServiceCall(String url, int method) {

        return this.makeServiceCall(url, method, null);
    }

    public String makeServiceCall(String url, int method,
                                  List<NameValuePair> params) {
        try {
            // http client
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpEntity httpEntity = null;
            HttpResponse httpResponse = null;

            // Checking http request method type
            if (method == POST) {
                HttpPost httpPost = new HttpPost(url);
                // adding post params
                if (params != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(params));
                }
                Log.e("URL =", "" + url);
                httpResponse = httpClient.execute(httpPost);
                Log.e("httpResponse", "" + httpResponse);
            } else if (method == GET) {
                // appending params to url
                if (params != null) {
                    String paramString = URLEncodedUtils
                            .format(params, "utf-8");
                    url += "?" + paramString;
                }
                HttpGet httpGet = new HttpGet(url);

                httpResponse = httpClient.execute(httpGet);

            }
            httpEntity = httpResponse.getEntity();
            Log.e("httpEntity", "" + httpEntity);
            response = EntityUtils.toString(httpEntity);

        } catch (UnsupportedEncodingException e) {
            Log.e("ERROR1", "" + e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            Log.e("ERROR2", "" + e);
            e.printStackTrace();
        } catch (IOException e) {
            Log.e("ERROR3", "" + e);
            e.printStackTrace();
        }

        return response;

    }
}
