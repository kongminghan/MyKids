package com.workshop2.mykids.Adapter;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.workshop2.mykids.Model.Hospital;
import com.workshop2.mykids.R;

import java.util.ArrayList;

/**
 * Created by MingHan on 14/11/2016.
 */

public class HospitalAdapter extends RecyclerView.Adapter<HospitalAdapter.HospitalViewHolder>{
    OnItemClickListener itemClickListener;
    private Context context;
    private ArrayList<Hospital> hospitals;
    private Hospital hospital;

    public HospitalAdapter(Context context, ArrayList<Hospital> hospitals){
        this.context = context;
        this.hospitals = hospitals;
    }

    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_hospital, parent, false);

        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HospitalViewHolder holder, int position) {
        holder.title.setText(hospitals.get(position).getHospitalName());
        holder.desc.setText(hospitals.get(position).getHospitalTelNum());

        Glide.with(context)
                .load(hospitals.get(position).getHospitalImage())
                .into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return hospitals.size();
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public Context context;
        public TextView title, desc;
        public ImageView imageView;

        public HospitalViewHolder(View itemView) {
            super(itemView);
            title = (TextView)itemView.findViewById(R.id.title);
            desc = (TextView)itemView.findViewById(R.id.desc);
            imageView = (ImageView)itemView.findViewById(R.id.imageView);
        }

        @Override
        public void onClick(View v) {

        }
    }

    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }
}