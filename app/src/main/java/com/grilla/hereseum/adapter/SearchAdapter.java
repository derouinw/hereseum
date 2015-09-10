package com.grilla.hereseum.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;

import java.util.List;

/**
 * Created by bill on 9/9/15.
 */
public class SearchAdapter extends ArrayAdapter<Place> {

    private Context mContext;
    private List<Place> mPlaces;

    public SearchAdapter(Context context, List<Place> objects) {
        super(context, -1, objects);

        mContext = context;
        mPlaces = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.search_item, parent, false);
        }

        Place place = mPlaces.get(position);

        TextView.class.cast(convertView.findViewById(R.id.text)).setText(place.getName());
        TextView.class.cast(convertView.findViewById(R.id.subtext)).setText(place.getLocationName());

        return convertView;
    }

    @Override
    public int getCount() {
        return mPlaces.size();
    }
}
