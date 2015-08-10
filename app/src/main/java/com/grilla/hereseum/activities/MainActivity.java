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
import com.grilla.hereseum.adapter.PostsAdapter;
import com.grilla.hereseum.helper.TaskCreator;
import com.grilla.hereseum.views.TimelineView;

import java.util.Calendar;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

public class MainActivity extends AppCompatActivity implements
        AbsListView.OnScrollListener, TimelineView.OnDateSelectedListener {
    private static final String TAG = "MainActivity";

    public static final String EXTRA_ACCESS_TOKEN = "access_token";

    private String mAccessToken;
    private Location mCurrentLocation;
    private boolean mLoadedFirst;
    private boolean mLoadingMore;
    private long mPreviousStartTime;
    private int mYear, mMonth;

    private TimelineView mTimeline;
    private RelativeLayout mEmptyArea;
    private ListView mList;
    private ProgressBar mLoading;
    private TextView mFooterText;
    private ProgressBar mFooterProgress;

    private PostsAdapter mAdapter;

    private LocationManager mLocationManager;
    private LocationListener mLocationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAccessToken = getIntent().getStringExtra(EXTRA_ACCESS_TOKEN);

        mTimeline = (TimelineView)findViewById(R.id.timeline);
        mTimeline.setOnDateSelectedListener(this);

        mLoadingMore = true;
        mAdapter = new PostsAdapter(this);
        mList = (ListView)findViewById(R.id.posts_list);
        mList.setAdapter(mAdapter);
        mList.setOnScrollListener(this);

        int footerHeight = (int)(getResources().getDimension(R.dimen.months_height) + getResources().getDimension(R.dimen.years_height));
        QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(mTimeline)
                .minFooterTranslation(footerHeight)
                .isSnappable(true)
                .build();
        scrollListener.registerExtraOnScrollListener(this);
        mList.setOnScrollListener(scrollListener);

        View footer = LayoutInflater.from(this).inflate(R.layout.view_list_footer, null);
        mFooterText = (TextView)footer.findViewById(R.id.footer_text);
        mFooterProgress = (ProgressBar)footer.findViewById(R.id.footer_loading);
        mList.addFooterView(footer);

        mEmptyArea = (RelativeLayout)findViewById(R.id.empty_notif);
        mLoading = (ProgressBar)findViewById(R.id.posts_loading);

        mPreviousStartTime = 0;

        Calendar cal = Calendar.getInstance();
        mMonth = cal.get(Calendar.MONTH);
        mYear = cal.get(Calendar.YEAR);

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

    private long getTime(int year, int month) {
        Calendar startDate = Calendar.getInstance();
        startDate.set(Calendar.YEAR, year);
        startDate.set(Calendar.MONTH, month);
        startDate.set(Calendar.DAY_OF_MONTH, -4); // ¯\_(ツ)_/¯
        return startDate.getTimeInMillis()/1000;
    }

    private void showPosts(int year, int month) {
        loadPosts(getTime(year, month));
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

    private void loadPosts(long startTime) {
        mLoadingMore = true;
        mPreviousStartTime = startTime;

        mList.setVisibility(View.GONE);
        mEmptyArea.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

        TaskCreator.getInstance(this).getPostsTask(mAccessToken, mCurrentLocation, startTime).continueWith(new Continuation<List<InstaPost>, Void>() {
            @Override
            public Void then(Task<List<InstaPost>> task) throws Exception {
                if (task.isFaulted()) {
                    Toast.makeText(MainActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    return null;
                }

                List<InstaPost> posts = task.getResult();
                mAdapter.setPosts(posts, mMonth);

                mEmptyArea.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
                mList.setVisibility(posts.isEmpty() ? View.GONE : View.VISIBLE);
                mList.setSelectionAfterHeaderView();

                mLoading.setVisibility(View.GONE);
                mLoadingMore = false;

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private void addPosts(long startTime) {
        mLoadingMore = true;
        mPreviousStartTime = startTime;

        TaskCreator.getInstance(this).getPostsTask(mAccessToken, mCurrentLocation, startTime).continueWith(new Continuation<List<InstaPost>, Void>() {
            @Override
            public Void then(Task<List<InstaPost>> task) throws Exception {
                if (task.isFaulted()) {
                    Toast.makeText(MainActivity.this, "Error loading posts", Toast.LENGTH_SHORT).show();
                    return null;
                }

                List<InstaPost> posts = task.getResult();
                mAdapter.addPosts(posts);

                mLoadingMore = false;
                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int lastItem = firstVisibleItem + visibleItemCount;
        if (lastItem == totalItemCount) {
            // At last item, load more
            if (!mLoadingMore) {
                long startTime = mAdapter.getLastTime();
                Log.d(TAG, "Loading more: " + startTime);

                if (startTime == mPreviousStartTime) {
                    Calendar cal = Calendar.getInstance();
                    if (mMonth == cal.get(Calendar.MONTH) && mYear == cal.get(Calendar.YEAR)) {
                        // current month
                        mFooterText.setText("You're all caught up");
                        mFooterText.setVisibility(View.VISIBLE);
                        mFooterProgress.setVisibility(View.GONE);
                    } else {
                        // other month
                        mFooterText.setText("That's all for that month");
                        mFooterText.setVisibility(View.VISIBLE);
                        mFooterProgress.setVisibility(View.GONE);
                    }
                } else {
                    // loading
                    mFooterText.setVisibility(View.GONE);
                    mFooterProgress.setVisibility(View.VISIBLE);
                    addPosts(startTime);
                }
            }
        }
    }

    @Override
    public void onDateSelected(int year, int month) {
        mYear = year;
        mMonth = month;

        if (mLoadedFirst) {
            showPosts(year, month);
        } else {
            mPreviousStartTime = getTime(year, month);
        }
    }
}
