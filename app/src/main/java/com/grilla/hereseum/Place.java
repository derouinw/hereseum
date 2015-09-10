package com.grilla.hereseum;

import android.location.Location;
import android.support.annotation.NonNull;

/**
 * Created by bill on 9/7/15.
 */
public class Place implements Comparable<Place> {

    private String mImagePath;
    private String mName;
    private String mLocationName;

    private Location mLocation;

    public Place(String imagePath, String name, String locationName, double latitude, double longitude) {
        mImagePath = imagePath;
        mName = name;
        mLocationName = locationName;

        mLocation = new Location("");
        mLocation.setLatitude(latitude);
        mLocation.setLongitude(longitude);
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

    public Location getLocation() {
        return mLocation;
    }

    @Override
    public int compareTo(@NonNull Place another) {
        return mName.compareTo(another.mName);
    }
}
