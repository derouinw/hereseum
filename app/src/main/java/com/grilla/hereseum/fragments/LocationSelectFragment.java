package com.grilla.hereseum.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.grilla.hereseum.adapter.PlacesAdapter;

/**
 * Created by bill on 9/7/15.
 */
public class LocationSelectFragment extends Fragment {

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

        return rootView;
    }
}
