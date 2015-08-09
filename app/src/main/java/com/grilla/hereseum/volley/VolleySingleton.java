package com.grilla.hereseum.volley;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by bill on 8/7/15.
 */
public class VolleySingleton {
    private static RequestQueue sRequestQueue;

    public synchronized static RequestQueue getInstance(Context context) {
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(context);
        }

        return sRequestQueue;
    }
}
