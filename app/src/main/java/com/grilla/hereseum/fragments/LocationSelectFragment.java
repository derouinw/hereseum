package com.grilla.hereseum.fragments;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ListView;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.adapter.PlacesAdapter;
import com.grilla.hereseum.adapter.SearchAdapter;

import java.util.ArrayList;

/**
 * Created by bill on 9/7/15.
 */
public class LocationSelectFragment extends Fragment {

    private View mOverlay;
    private View mVoiceSearch;
    private EditText mSearchBox;
    private ListView mSearchList;

    private Activity mContext;
    private MainActivityInteraction mActivity;
    private PlacesAdapter mPlacesAdapter;
    private SearchAdapter mSearchAdapter;

    public interface MainActivityInteraction {
        void setPage(int page, Place place);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mActivity = (MainActivityInteraction)activity;
            mContext = activity;
        } catch (ClassCastException e) {
            android.util.Log.e("LocationSelectFragment", "Error casting activity", e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_location, container, false);

        RecyclerView cardList = (RecyclerView)rootView.findViewById(R.id.featured_places);
        cardList.setHasFixedSize(true);
        cardList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        mPlacesAdapter = new PlacesAdapter(getActivity().getApplicationContext(), mActivity);
        cardList.setAdapter(mPlacesAdapter);

        mOverlay = rootView.findViewById(R.id.overlay);
        mOverlay.setVisibility(View.INVISIBLE);
        mOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

        mVoiceSearch = rootView.findViewById(R.id.voice_search);
        mVoiceSearch.setVisibility(View.INVISIBLE);

        mSearchBox = (EditText)rootView.findViewById(R.id.search_edit);
        mSearchBox.setVisibility(View.INVISIBLE);
        mSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    closeSearch();
                }
            }
        });

        mSearchList = (ListView)rootView.findViewById(R.id.search_list);
        mSearchList.setVisibility(View.INVISIBLE);

        ArrayList<Place> places = new ArrayList<>();
        places.add(new Place("", "Tommy Trojan", "Los Angeles, CA", 34.020549, -118.285434));
        places.add(new Place("", "Eiffel Tower", "Paris, France", 48.858309, 2.294399));
        mSearchAdapter = new SearchAdapter(mContext, places);
        mSearchList.setAdapter(mSearchAdapter);

        View searchButton = rootView.findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSearch();
            }
        });

        View pinButton = rootView.findViewById(R.id.pin_drop);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();

                mActivity.setPage(0, null);
            }
        });

        return rootView;
    }

    private void openSearch() {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mSearchBox,
                mSearchBox.getWidth(),
                mSearchBox.getHeight()/2,
                0,
                (float) Math.hypot(mSearchBox.getWidth(), mSearchBox.getHeight()));
        animator.setDuration(200);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        mOverlay.setVisibility(View.VISIBLE);
        mSearchBox.setVisibility(View.VISIBLE);
        mVoiceSearch.setVisibility(View.VISIBLE);
        mSearchList.setVisibility(View.VISIBLE);

        mSearchBox.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInputFromWindow(mSearchBox.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

    }

    private void closeSearch() {
        Animator animator = ViewAnimationUtils.createCircularReveal(
                mSearchBox,
                mSearchBox.getWidth(),
                mSearchBox.getHeight()/2,
                (float) Math.hypot(mSearchBox.getWidth(), mSearchBox.getHeight()),
                0);
        animator.setDuration(100);
        animator.setInterpolator(new LinearInterpolator());
        animator.start();

        ViewCompat.postOnAnimationDelayed(mSearchBox, new Runnable() {
            @Override
            public void run() {
                mSearchBox.setVisibility(View.INVISIBLE);
            }
        }, 100);

        mOverlay.setVisibility(View.INVISIBLE);
        mVoiceSearch.setVisibility(View.INVISIBLE);
        mSearchList.setVisibility(View.INVISIBLE);

        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(mSearchBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

        mSearchBox.setText("");
    }
}
