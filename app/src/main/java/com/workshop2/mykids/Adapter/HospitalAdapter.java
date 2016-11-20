package com.workshop2.mykids.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.workshop2.mykids.HospitalDetailActivity;
import com.workshop2.mykids.model.Hospital;
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
    private int width;

    public HospitalAdapter(Context context, ArrayList<Hospital> hospitals, int width){
        this.context = context;
        this.hospitals = hospitals;
        this.width = width;
    }

    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_hospital, parent, false);
        int item_width = width - (width/100 *11);
        view.setLayoutParams(new RecyclerView.LayoutParams(item_width, ViewGroup.LayoutParams.WRAP_CONTENT));

        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HospitalViewHolder holder, final int position) {
        holder.title.setText(hospitals.get(position).getHospitalName());
        holder.desc.setText(hospitals.get(position).getHospitalTelNum());

        Glide.with(context)
                .load(hospitals.get(position).getHospitalImage())
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, HospitalDetailActivity.class);
                intent.putExtra("hName", hospitals.get(position).getHospitalName());
                intent.putExtra("hAddress", hospitals.get(position).getHospitalAddress());
                intent.putExtra("hImage", hospitals.get(position).getHospitalImage());
                intent.putExtra("hTel", hospitals.get(position).getHospitalTelNum());
//                intent.putExtra("hPostCode", hospitals.get(position).getHospitalPoscode());
                intent.putExtra("lat", hospitals.get(position).getLatitute());
                intent.putExtra("longt", hospitals.get(position).getLongtitute());
                ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, ((HospitalViewHolder)holder).imageView, "tImageView");
                context.startActivity(intent, optionsCompat.toBundle());
            }
        });
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