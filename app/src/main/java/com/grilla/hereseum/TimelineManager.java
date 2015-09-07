package com.grilla.hereseum;

import android.content.Context;
import android.location.Location;
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
import com.grilla.hereseum.adapter.PostsAdapter;
import com.grilla.hereseum.helper.TaskCreator;
import com.grilla.hereseum.views.TimelineView;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import bolts.Continuation;
import bolts.Task;

/**
 * Created by bill on 9/7/15.
 */
public class TimelineManager implements TimelineView.OnDateSelectedListener, AbsListView.OnScrollListener {
    private static final String TAG = "TimelineManager";

    private Context mContext;

    private TimelineView mTimeline;
    private RelativeLayout mEmptyArea;
    private ListView mList;
    private ProgressBar mLoading;
    private TextView mFooterText;
    private ProgressBar mFooterProgress;

    private PostsAdapter mAdapter;

    private Calendar mSelectedDate;
    private boolean mLoadingMore;
    private boolean mLocationLoaded;
    private long mPreviousStartTime;

    private String mAccessToken;
    private Location mCurrentLocation;

    public TimelineManager(View rootView, String accessToken) {
        mContext = rootView.getContext();
        mAccessToken = accessToken;
        mLoadingMore = true;

        setupViews(rootView);
        setupDate();
    }

    private void setupViews(View rootView) {
        mTimeline = (TimelineView)rootView.findViewById(R.id.timeline);
        mTimeline.setOnDateSelectedListener(this);

        mAdapter = new PostsAdapter(mContext);

        mList = (ListView)rootView.findViewById(R.id.posts_list);
        mList.setAdapter(mAdapter);
        mList.setOnScrollListener(this);

        int footerHeight = (int)(mContext.getResources().getDimension(R.dimen.months_height)
                + mContext.getResources().getDimension(R.dimen.years_height));

        QuickReturnListViewOnScrollListener scrollListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.FOOTER)
                .footer(mTimeline)
                .minFooterTranslation(footerHeight)
                .isSnappable(true)
                .build();
        scrollListener.registerExtraOnScrollListener(this);
        mList.setOnScrollListener(scrollListener);

        View footer = LayoutInflater.from(mContext).inflate(R.layout.view_list_footer, null);
        mFooterText = (TextView)footer.findViewById(R.id.footer_text);
        mFooterProgress = (ProgressBar)footer.findViewById(R.id.footer_loading);
        mList.addFooterView(footer);

        mEmptyArea = (RelativeLayout)rootView.findViewById(R.id.empty_notif);
        mLoading = (ProgressBar)rootView.findViewById(R.id.posts_loading);
    }

    // Set date to today, at beginning of day
    private void setupDate() {
        mSelectedDate = Calendar.getInstance();
        mSelectedDate.set(GregorianCalendar.HOUR_OF_DAY, 0);
        mSelectedDate.set(GregorianCalendar.MINUTE, 0);
        mSelectedDate.set(GregorianCalendar.SECOND, 0);
        mSelectedDate.set(GregorianCalendar.MILLISECOND,0);

        mPreviousStartTime = mSelectedDate.getTimeInMillis();
    }

    public void loadPosts() {
        mLoadingMore = true;

        mList.setVisibility(View.GONE);
        mEmptyArea.setVisibility(View.GONE);
        mLoading.setVisibility(View.VISIBLE);

        TaskCreator.getInstance(mContext).getPostsTask(mAccessToken, mCurrentLocation, mSelectedDate.getTimeInMillis()/1000)
                .continueWith(new Continuation<List<InstaPost>, Void>() {
                    @Override
                    public Void then(Task<List<InstaPost>> task) throws Exception {
                        if (task.isFaulted()) {
                            Toast.makeText(mContext, mContext.getString(R.string.main_loading_error), Toast.LENGTH_SHORT).show();
                            return null;
                        }

                        List<InstaPost> posts = task.getResult();
                        mAdapter.setPosts(posts, mSelectedDate.get(Calendar.MONTH));

                        mEmptyArea.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
                        mList.setVisibility(posts.isEmpty() ? View.GONE : View.VISIBLE);
                        mList.setSelectionAfterHeaderView();

                        mLoading.setVisibility(View.GONE);
                        mLoadingMore = false;

                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    public void addPosts(long startTime) {
        mLoadingMore = true;

        TaskCreator.getInstance(mContext).getPostsTask(mAccessToken, mCurrentLocation, startTime)
                .continueWith(new Continuation<List<InstaPost>, Void>() {
                    @Override
                    public Void then(Task<List<InstaPost>> task) throws Exception {
                        if (task.isFaulted()) {
                            Toast.makeText(mContext, mContext.getString(R.string.main_loading_error), Toast.LENGTH_SHORT).show();
                            return null;
                        }

                        List<InstaPost> posts = task.getResult();
                        mAdapter.addPosts(posts);

                        mLoadingMore = false;
                        return null;
                    }
                }, Task.UI_THREAD_EXECUTOR);
    }

    public void updateLocation(Location currentLocation) {
        mCurrentLocation = currentLocation;
        mLocationLoaded = true;
    }

    @Override
    public void onDateSelected(int year, int month) {
        mSelectedDate.set(Calendar.MONTH, month);
        mSelectedDate.set(Calendar.YEAR, year);
        mPreviousStartTime = mSelectedDate.getTimeInMillis();

        if (mLocationLoaded) {
            loadPosts();
        }
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

                // Reached the end
                if (startTime == mPreviousStartTime) {
                    Calendar cal = Calendar.getInstance();
                    if (mSelectedDate.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
                            && mSelectedDate.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                        // current month
                        mFooterText.setText(mContext.getString(R.string.main_footer_current));
                        mFooterText.setVisibility(View.VISIBLE);
                        mFooterProgress.setVisibility(View.GONE);
                    } else {
                        // other month
                        mFooterText.setText(mContext.getString(R.string.main_footer_old));
                        mFooterText.setVisibility(View.VISIBLE);
                        mFooterProgress.setVisibility(View.GONE);
                    }
                } else {
                    // loading
                    mFooterText.setVisibility(View.GONE);
                    mFooterProgress.setVisibility(View.VISIBLE);
                    mPreviousStartTime = startTime;
                    addPosts(startTime);
                }
            }
        }
    }
}
