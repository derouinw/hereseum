package com.grilla.hereseum.helper;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.android.volley.VolleyError;
import com.grilla.hereseum.InstaPost;
import com.grilla.hereseum.activities.InstagramAuthActivity;
import com.grilla.hereseum.volley.VolleyListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by bill on 8/7/15.
 */
public class TaskCreator {
    private static final int SEARCH_DISTANCE = 42;
    private static final String TAG = "TaskCreator";
    private static final String INSTA_TOKEN_URL = "https://api.instagram.com/oauth/access_token";
    private static final String INSTA_SEARCH_URL = "https://api.instagram.com/v1/media/search";

    private static TaskCreator sInstance;

    private Requester mRequester;

    private TaskCreator(Context context) {
        mRequester = Requester.getInstance(context);
    }

    public static synchronized TaskCreator getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new TaskCreator(context);
        }

        return sInstance;
    }

    /**
     * Retrieve the access token from the insta server
     * @param authCode auth code received from auth flow
     * @return access token
     */
    public Task<String> getAccessTokenTask(final String authCode) {
        final Task<String>.TaskCompletionSource tcs = Task.create();

        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Map<String, String> params = new HashMap<>();
                params.put("client_id", InstagramAuthActivity.INSTA_CLIENT_ID);
                params.put("client_secret", InstagramAuthActivity.INSTA_CLIENT_SECRET);
                params.put("grant_type", "authorization_code");
                params.put("redirect_uri", InstagramAuthActivity.INSTA_REDIRECT_URI);
                params.put("code", authCode);

                mRequester.makePostRequest(INSTA_TOKEN_URL, params, new VolleyListener() {
                    @Override
                    public void onSuccess(String response) {
                        try {
                            JSONObject jo = new JSONObject(response);
                            tcs.setResult(jo.getString("access_token"));
                        } catch (JSONException e) {
                            tcs.setError(e);
                        }
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        tcs.setError(error);
                    }
                });

                return null;
            }
        });

        return tcs.getTask();
    }

    /**
     * Get list of posts from insta
     * @param accessToken access token from auth flow
     * @return list of posts
     */
    public Task<List<InstaPost>> getPostsTask(final String accessToken, final Location loc, long startTime) {
        return getPostsFromInstaTask(accessToken, loc, startTime).onSuccessTask(new Continuation<String, Task<List<InstaPost>>>() {
            @Override
            public Task<List<InstaPost>> then(Task<String> task) throws Exception {
                return parsePostsTask(task.getResult());
            }
        });
    }

    /**
     * Async volley call to get posts from insta
     * @param accessToken access token from auth flow
     * @return server response
     */
    private Task<String> getPostsFromInstaTask(final String accessToken, final Location loc, final long startTime) {
        final Task<String>.TaskCompletionSource tcs = Task.create();

        Task.callInBackground(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                String url = INSTA_SEARCH_URL +
                        "?lat=" + loc.getLatitude() +
                        "&lng=" + loc.getLongitude() +
                        "&distance=" + SEARCH_DISTANCE +
                        "&access_token=" + accessToken;
                if (startTime != 0) {
                    url += "&min_timestamp=" + startTime;
                }

                mRequester.makeGetRequest(url, new VolleyListener() {
                    @Override
                    public void onSuccess(String response) {
                        Log.d(TAG, "Response: " + response);
                        tcs.setResult(response);
                    }

                    @Override
                    public void onFailure(VolleyError error) {
                        tcs.setError(error);
                    }
                });

                return null;
            }
        });

        return tcs.getTask();
    }

    /**
     * Parse posts from json into posts objects
     * @param response string response from server
     * @return list of insta post objects
     */
    private Task<List<InstaPost>> parsePostsTask(final String response) {
        final Task<List<InstaPost>>.TaskCompletionSource tcs = Task.create();

        Task.callInBackground(new Callable<List<InstaPost>>() {
            @Override
            public List<InstaPost> call() throws Exception {
                JSONArray postsData = new JSONObject(response).getJSONArray("data");
                List<InstaPost> posts = new ArrayList<>();

                Log.d(TAG, "Loading " + postsData.length() + " posts");
                for (int i = 0, l = postsData.length(); i < l; i++) {
                    try {
                        InstaPost post = InstaPost.fromJson(postsData.getJSONObject(i));
                        if (post.getType().equals("image")) {
                            posts.add(post);
                        }
                    } catch (JSONException e) {
                        // if json is malformed, ignore it
                    }
                }

                Collections.sort(posts);

                tcs.setResult(posts);
                return null;
            }
        });

        return tcs.getTask();
    }
}
