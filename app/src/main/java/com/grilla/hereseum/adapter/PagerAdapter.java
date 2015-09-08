package com.grilla.hereseum.adapter;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.grilla.hereseum.fragments.LocationSelectFragment;
import com.grilla.hereseum.fragments.TimelineViewFragment;

/**
 * Created by bill on 9/7/15.
 */
public class PagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "PagerAdapter";
    private static final int NUM_PAGES = 2;

    private String mAccessToken;
    private Location mCurrentLocation;
    private LocationUpdateListener mLocationListener;

    public PagerAdapter(FragmentManager fm, String accessToken) {
        super(fm);

        mAccessToken = accessToken;
    }

    @Override
    public Fragment getItem(int position) {
        switch(position) {
            case 0:
                TimelineViewFragment timelineFrag = new TimelineViewFragment();
                Bundle args = new Bundle();
                args.putString(TimelineViewFragment.EXTRA_ACCESS_TOKEN, mAccessToken);
                timelineFrag.setArguments(args);
                mLocationListener = timelineFrag;
                return timelineFrag;
            case 1:
                return new LocationSelectFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }

    public void updateLocation(Location location) {
        mCurrentLocation = location;

        if (mLocationListener != null) {
            mLocationListener.locationUpdated(location);
        }
    }

    public interface LocationUpdateListener {
        void locationUpdated(Location location);
    }
}
