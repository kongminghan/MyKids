//package com.workshop2.mykids.Adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.bumptech.glide.Glide;
//import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.workshop2.mykids.Model.Vaccine;
//import com.workshop2.mykids.R;
//
//import java.util.ArrayList;
//
///**
// * Created by MingHan on 5/10/2016.
// */
//
//public class VaccineAdapter extends RecyclerView.Adapter<VaccineAdapter.MyViewHolder>{
//
//    private Context mContext;
//    private ArrayList<Vaccine> vaccines;
//    private Vaccine vaccine;
//    OnItemClickListener mItemClickListener;
//
//    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
//        public TextView vaccineName;
//        public ImageView vaccineImage;
//        public LinearLayout vaccineHolder;
//        public LinearLayout vaccineNameHolder;
//
//        public MyViewHolder(View itemView) {
//            super(itemView);
//
//            vaccineName = (TextView)itemView.findViewById(R.id.vaccineName);
//            vaccineImage = (ImageView)itemView.findViewById(R.id.vaccineImage);
//            vaccineHolder = (LinearLayout)itemView.findViewById(R.id.vaccineHolder);
//            vaccineNameHolder = (LinearLayout)itemView.findViewById(R.id.vaccineNameHolder);
//            vaccineHolder.setOnClickListener(this);
//        }
//
//        @Override
//        public void onClick(View v) {
//            if (mItemClickListener != null) {
//                mItemClickListener.onItemClick(itemView, getPosition());
//            }
//        }
//    }
//
//    public interface OnItemClickListener{
//        void onItemClick(View view, int position);
//    }
//
//    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
//        this.mItemClickListener = mItemClickListener;
//    }
//
//    public VaccineAdapter(Context mContext, ArrayList<Vaccine> vaccines){
//        this.mContext = mContext;
//        this.vaccines = vaccines;
//    }
//
//    @Override
//    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View itemView = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.card_vaccine, parent, false);
//
//        return new MyViewHolder(itemView);
//    }
//
//    @Override
//    public void onBindViewHolder(final MyViewHolder holder, int position) {
//        vaccine = vaccines.get(position);
//        holder.vaccineName.setText(vaccine.getV_name());
////
////        Context context = mContext;
////        int duration = Toast.LENGTH_SHORT;
////        Toast toast = Toast.makeText(context,"binding" , duration);
////        toast.show();
//
//        Glide.with(mContext)
//                .load(vaccine.getV_image())
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.vaccineImage);
//
////        holder.vaccineHolder.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////
////            }
////        });
//
////        Bitmap photo = BitmapFactory.decodeResource(mContext.getResources(), vaccine.getImageResourceId(mContext));
////
////        Palette.generateAsync(photo, new Palette.PaletteAsyncListener() {
////            public void onGenerated(Palette palette) {
////                int bgColor = palette.getMutedColor(mContext.getResources().getColor(android.R.color.black));
////                holder.placeNameHolder.setBackgroundColor(bgColor);
////            }
////        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return vaccines.size();
//    }
//}