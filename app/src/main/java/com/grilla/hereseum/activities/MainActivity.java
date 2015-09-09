package com.grilla.hereseum.activities;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.adapter.PagerAdapter;
import com.grilla.hereseum.fragments.LocationSelectFragment;

public class MainActivity extends AppCompatActivity implements LocationSelectFragment.MainActivityInteraction {
    private static final String TAG = "MainActivity";

    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    private String mAccessToken;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private Location mCurrentLocation;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccessToken = getIntent().getStringExtra(EXTRA_ACCESS_TOKEN);

        mPager = (ViewPager)findViewById(R.id.pager);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), mAccessToken);
        mPager.setAdapter(mPagerAdapter);

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
                mPagerAdapter.updateLocation(location);
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {}

            public void onProviderEnabled(String provider) {}

            public void onProviderDisabled(String provider) {}
        };
    }

    @Override
    public void setPage(int page, Place place) {
        mPager.setCurrentItem(page);

        if (page == 0 && place != null) {
            mPagerAdapter.viewOtherLocation(place);
        }
    }
}
