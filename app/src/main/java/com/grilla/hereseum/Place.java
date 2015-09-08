package com.grilla.hereseum;

/**
 * Created by bill on 9/7/15.
 */
public class Place {

    private String mImagePath;
    private String mName;
    private String mLocationName;

    public Place(String imagePath, String name, String locationName) {
        mImagePath = imagePath;
        mName = name;
        mLocationName = locationName;
    }

    public String getImagePath() {
        return mImagePath;
    }

    public String getName() {
        return mName;
    }

    public String getLocationName() {
        return mLocationName;
    }
}
