package com.grilla.hereseum.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grilla.hereseum.R;
import com.grilla.hereseum.adapter.PlacesAdapter;

/**
 * Created by bill on 9/7/15.
 */
public class LocationSelectFragment extends Fragment {

    PlacesAdapter mPlacesAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_select_location, container, false);

        RecyclerView cardList = (RecyclerView)rootView.findViewById(R.id.featured_places);
        cardList.setHasFixedSize(true);
        cardList.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

        mPlacesAdapter = new PlacesAdapter(getActivity().getApplicationContext());
        cardList.setAdapter(mPlacesAdapter);

        return rootView;
    }
}
