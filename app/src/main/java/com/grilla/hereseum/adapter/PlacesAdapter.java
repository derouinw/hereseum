package com.grilla.hereseum.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.grilla.hereseum.Place;
import com.grilla.hereseum.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by bill on 9/7/15.
 */
public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<Place> mPlaces;

    private int mImageSize;

    public PlacesAdapter(Context context) {
        super();

        mContext = context;
        mPlaces = new ArrayList<>();

        Place tommyTrojan = new Place("https://upload.wikimedia.org/wikipedia/commons/0/06/Tommy_Trojan_statue_closeup.jpg",
                "Tommy Trojan", "Los Angeles, CA");
        Place eiffelTower = new Place("http://www.premiumtours.co.uk/images/product/original/68_1.jpg",
                "Eiffel Tower", "Paris, France");

        mPlaces.add(tommyTrojan);
        mPlaces.add(eiffelTower);

        mImageSize = (int)context.getResources().getDimension(R.dimen.place_image_size);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.view_place, parent, false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Place place = mPlaces.get(position);
        holder.mTitle.setText(place.getName());
        holder.mSubtitle.setText("Found in " + place.getLocationName());

        Picasso.with(mContext).load(place.getImagePath()).resize(mImageSize, mImageSize).centerCrop().into(holder.mImage);
    }

    @Override
    public int getItemCount() {
        return mPlaces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView mImage;
        TextView mTitle;
        TextView mSubtitle;

        public ViewHolder(View itemView) {
            super(itemView);

            mImage = (ImageView)itemView.findViewById(R.id.place_image);
            mTitle = (TextView)itemView.findViewById(R.id.place_title);
            mSubtitle = (TextView)itemView.findViewById(R.id.place_subtitle);
        }
    }
}
