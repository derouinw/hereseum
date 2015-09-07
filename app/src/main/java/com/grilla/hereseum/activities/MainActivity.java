package com.grilla.hereseum.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.grilla.hereseum.InstaPost;
import com.grilla.hereseum.R;
import com.grilla.hereseum.TimelineManager;
import com.grilla.hereseum.adapter.PostsAdapter;
import com.grilla.hereseum.helper.TaskCreator;
import com.grilla.hereseum.views.TimelineView;

import java.util.Calendar;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    private String mAccessToken;

    private boolean mLoadedFirst;

    private TimelineManager mTimelineManager;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccessToken = getIntent().getStringExtra(EXTRA_ACCESS_TOKEN);
        mTimelineManager = new TimelineManager(findViewById(R.id.root_view), mAccessToken);

        setupLocation();
    }

    @Override
    public void onResume() {
        super.onResume();
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);
    }

    @Override
    public void onPause() {
        mLocationManager.removeUpdates(mLocationListener);
        super.onPause();
    }

    private void setupLocation() {
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        mLocationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // Called when a new location is found by the network location provider.
                mCurrentLocation = location;

                if (!mLoadedFirst) {
                    findViewById(R.id.location_waiting).setVisibility(View.GONE);
                    mLoadedFirst = true;
                    Log.d(TAG, "Loading posts... ");
                    loadPosts(mPreviousStartTime);
                }
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }
}
