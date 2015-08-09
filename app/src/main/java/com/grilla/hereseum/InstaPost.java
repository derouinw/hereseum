package com.grilla.hereseum;

import android.text.format.DateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by bill on 8/7/15.
 */
public class InstaPost implements Comparable<InstaPost> {
    private String mType;

    //private Location mLocation;
    //private String mLocationName;
    //private int mLocationId;

    //private List<String> mComments;

    private String mAvatarUrl;
    private String mUser;

    private String mCreated;
    private long mCreatedTime;
    private int mMonth;

    private String mLink;

    private int mLikes;

    private String mImageUrl;

    private String mCaption;

    private InstaPost() {}

    public static InstaPost fromJson(JSONObject object) throws JSONException {
        InstaPost post = new InstaPost();

        post.mType = object.getString("type");

        if (!post.mType.equals("image")) {
            return post;
        }

        JSONObject user = object.getJSONObject("user");
        post.mAvatarUrl = user.getString("profile_picture");
        post.mUser = user.getString("username");

        post.mCreatedTime = Long.valueOf(object.getString("created_time"));
        Date created = new Date(post.mCreatedTime * 1000);
        post.mCreated = DateFormat.format("MM.dd.yyyy", created).toString();
        post.mMonth = created.getMonth();

        post.mLink = object.getString("link");
        post.mLikes = object.getJSONObject("likes").getInt("count");
        post.mImageUrl = object.getJSONObject("images").getJSONObject("standard_resolution").getString("url");
        post.mCaption = object.getJSONObject("caption").getString("text");

        return post;
    }

    public String getType() {
        return mType;
    }

    public String getCreated() {
        return mCreated;
    }

    public long getCreatedTime() {
        return mCreatedTime;
    }

    public int getMonth() {
        return mMonth;
    }

    public String getLink() {
        return mLink;
    }

    public int getLikes() {
        return mLikes;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public String getCaption() {
        return mCaption;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getUser() {
        return mUser;
    }

    @Override
    public int compareTo(InstaPost another) {
        if (mCreatedTime < another.mCreatedTime) {
            return -1;
        } else if (mCreatedTime > another.mCreatedTime) {
            return 1;
        }
        return 0;
    }
}
