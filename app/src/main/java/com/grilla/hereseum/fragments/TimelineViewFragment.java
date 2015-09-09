package com.grilla.hereseum.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.TimelineManager;
import com.grilla.hereseum.adapter.PagerAdapter;

/**
 * Created by bill on 9/7/15.
 */
public class TimelineViewFragment extends Fragment implements PagerAdapter.LocationUpdateListener {
    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    private String mAccessToken;
    private TimelineManager mTimelineManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mAccessToken = args.getString(EXTRA_ACCESS_TOKEN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_view, container, false);

        mTimelineManager = new TimelineManager(rootView, mAccessToken);

        return rootView;
    }

    @Override
    public void locationUpdated(Location location) {
        mTimelineManager.updateLocation(location);
    }

    public void viewOtherLocation(Place place) {
        mTimelineManager.viewOtherLocation(place);
    }
}
