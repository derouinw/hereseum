package com.grilla.hereseum.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.grilla.hereseum.R;

/**
 * Created by bill on 9/7/15.
 */
public class LocationSelectFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_location, container, false);
    }
}
