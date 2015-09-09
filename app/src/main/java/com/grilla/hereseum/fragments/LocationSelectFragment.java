package com.grilla.hereseum.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.adapter.PlacesAdapter;

/**
 * Created by bill on 9/7/15.
 */
public class LocationSelectFragment extends Fragment {

    private View mOverlay;
    private View mVoiceSearch;
    private EditText mSearchBox;

    private Activity mContext;
    private MainActivityInteraction mActivity;
    private PlacesAdapter mPlacesAdapter;

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

        mVoiceSearch = rootView.findViewById(R.id.voice_search);
        mVoiceSearch.setVisibility(View.INVISIBLE);

        mSearchBox = (EditText)rootView.findViewById(R.id.search_edit);
        mSearchBox.setVisibility(View.INVISIBLE);
        mSearchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mOverlay.setVisibility(View.INVISIBLE);
                    mSearchBox.setVisibility(View.INVISIBLE);
                    mVoiceSearch.setVisibility(View.INVISIBLE);

                    InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(mSearchBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
            }
        });

        View searchButton = rootView.findViewById(R.id.search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOverlay.setVisibility(View.VISIBLE);
                mSearchBox.setVisibility(View.VISIBLE);
                mVoiceSearch.setVisibility(View.VISIBLE);
                if (mSearchBox.requestFocus()) {
                    mContext.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });

        View pinButton = rootView.findViewById(R.id.pin_drop);
        pinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOverlay.setVisibility(View.INVISIBLE);
                mSearchBox.setVisibility(View.INVISIBLE);
                mVoiceSearch.setVisibility(View.INVISIBLE);

                InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(mSearchBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                mActivity.setPage(0, null);
            }
        });

        return rootView;
    }
}
