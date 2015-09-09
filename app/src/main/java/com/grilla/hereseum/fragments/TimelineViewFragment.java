package com.grilla.hereseum.fragments;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.TimelineManager;
import com.grilla.hereseum.activities.MainActivity;
import com.grilla.hereseum.adapter.PagerAdapter;

/**
 * Created by bill on 9/7/15.
 */
public class TimelineViewFragment extends Fragment implements PagerAdapter.LocationUpdateListener {
    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    private String mAccessToken;
    private TimelineManager mTimelineManager;

    private LocationSelectFragment.MainActivityInteraction mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        mAccessToken = args.getString(EXTRA_ACCESS_TOKEN);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActivity = (LocationSelectFragment.MainActivityInteraction)activity;
        } catch (ClassCastException e) {
            android.util.Log.e("TimelineViewFragment", "Failed to cast activity", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timeline_view, container, false);

        mTimelineManager = new TimelineManager(rootView, mAccessToken);

        View airplane = rootView.findViewById(R.id.location_button);
        airplane.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.setPage(1, null);
            }
        });

        return rootView;
    }

    @Override
    public void locationUpdated(Location location) {
        mTimelineManager.updateLocation(location);
    }

    public void viewOtherLocation(Place place) {
        mTimelineManager.viewOtherLocation(place);
    }

    public void viewCurrentLocation() {
        mTimelineManager.viewCurrentLocation();
    }
}
