package com.grilla.hereseum.volley;

import com.android.volley.VolleyError;

/**
 * Created by bill on 8/7/15.
 */
public interface VolleyListener {
    void onSuccess(String response);
    void onFailure(VolleyError error);
}
