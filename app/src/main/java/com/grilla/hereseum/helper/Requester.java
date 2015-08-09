package com.grilla.hereseum.helper;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.grilla.hereseum.volley.VolleyListener;
import com.grilla.hereseum.volley.VolleySingleton;

import java.util.Map;

/**
 * Created by bill on 8/7/15.
 */
public class Requester {
    private static final String TAG = "Requester";

    private static Requester sInstance;

    private RequestQueue mRequestQueue;

    private Requester(Context context) {
        mRequestQueue = VolleySingleton.getInstance(context);
    }

    public static synchronized Requester getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new Requester(context);
        }

        return sInstance;
    }

    public void makePostRequest(String url, final Map<String, String> params, final VolleyListener listener) {
        Response.Listener<String> volleyListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.onSuccess(s);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure(volleyError);
            }
        };

        StringRequest request = new StringRequest(Request.Method.POST, url, volleyListener, errorListener) {
            @Override
            protected Map<String,String> getParams() {
                return params;
            }
        };

        mRequestQueue.add(request);
    }

    public void makeGetRequest(String url, final VolleyListener listener) {
        Response.Listener<String> volleyListener = new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                listener.onSuccess(s);
            }
        };

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                listener.onFailure(volleyError);
            }
        };

        StringRequest request = new StringRequest(Request.Method.GET, url, volleyListener, errorListener);
        mRequestQueue.add(request);
    }
}
