package com.grilla.hereseum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by bill on 9/9/15.
 */
public class SearchAdapter extends ArrayAdapter<Place> {

    private Context mContext;
    private List<Place> mAllPlaces;
    private List<Place> mFilteredPlaces;

    public SearchAdapter(Context context, List<Place> objects) {
        super(context, -1, objects);

        mContext = context;
        mAllPlaces = objects;
        Collections.sort(mAllPlaces);
        mFilteredPlaces = new ArrayList<>(mAllPlaces);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
        }

        Place place = mFilteredPlaces.get(position);

        TextView.class.cast(convertView.findViewById(R.id.text)).setText(place.getName());
        TextView.class.cast(convertView.findViewById(R.id.subtext)).setText(place.getLocationName());

        return convertView;
    }

    @Override
    public int getCount() {
        return mFilteredPlaces.size();
    }

    public void onTextChanged(String text) {
        mFilteredPlaces.clear();

        for (Place p : mAllPlaces) {
            if (p.getName().toLowerCase().contains(text)) {
                mFilteredPlaces.add(p);
            }
        }

        notifyDataSetChanged();
    }

    public Place getPlace(int position) {
        return mFilteredPlaces.get(position);
    }
}
