package com.example.burhan.gpstracker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by burha on 11-01-2017.
 */

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.MyViewHolder> {
    private Context mContext;
    private List<Location> locationList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView location, date;


        public MyViewHolder(View view) {
            super(view);
            location = (TextView) view.findViewById(R.id.tvLocation);
            date = (TextView) view.findViewById(R.id.tvDate);
        }
    }

    public LocationAdapter(Context mContext, List<Location> locationList) {
        this.mContext = mContext;
        this.locationList = locationList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.location_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Location l = locationList.get(position);
        holder.location.setText(l.getLocation());
        holder.date.setText(l.getDate());
    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }
}
