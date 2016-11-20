package com.workshop2.mykids.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.workshop2.mykids.R;
import com.workshop2.mykids.VaccineBottomSheetDialog;
import com.workshop2.mykids.model.Event;
import com.workshop2.mykids.model.Schedule;
import com.workshop2.mykids.other.CircleTransform;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.workshop2.mykids.model.Event.EVENT;
import static com.workshop2.mykids.model.Event.TITLE;

/**
 * Created by MingHan on 20/11/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    private ArrayList<Event> kids;

    public EventAdapter(Context context, ArrayList<Event> kids){
        this.context = context;
        this.kids = kids;
    }

    @Override
    public int getItemViewType(int position){
        if(kids != null){
            return kids.get(position).getType();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType){
            case TITLE:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_event, parent, false);
                return new TitleViewHolder(itemView);
            case EVENT:
                itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_event, parent, false);
                return new MyViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final Event event = kids.get(position);
        switch (event.getType()){
            case TITLE:
                ((TitleViewHolder)holder).tvKidName.setText(event.getKid_name());
                break;

            case EVENT:
                Calendar c = Calendar.getInstance();
                SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                Date date = null;
                try {
                    date = se.parse(event.getS_date()+" "+event.getS_time());
                    c.setTime(date);
                    ((MyViewHolder)holder).tvTime.setText(new SimpleDateFormat("EEE, d MMM yyyy hh:mm a").format(c.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((MyViewHolder)holder).tvName.setText(event.getKid_name()+"'s "+event.getS_name()+" vaccination");

                int bgColor = getMatColor("600");
                TextDrawable drawable1 = TextDrawable.builder()
                        .buildRound(event.getS_name().substring(0,1), bgColor); // radius in px
                ((MyViewHolder)holder).imageView.setImageDrawable(drawable1);

//                Glide.with(context)
//                        .load(event.getKid_image())
//                        .crossFade()
//                        .thumbnail(0.5f)
//                        .bitmapTransform(new CircleTransform(context))
//                        .into(((MyViewHolder)holder).imageView);

                ((MyViewHolder)holder).view.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).eventHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "Clickable", Toast.LENGTH_SHORT).show();
                    }
                });
        }
    }

    private int getMatColor(String typeColor)
    {
        int returnColor = Color.BLACK;
        int arrayId = context.getResources().getIdentifier("mdcolor_" + typeColor, "array", context.getPackageName());

        if (arrayId != 0)
        {
            TypedArray colors = context.getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.BLACK);
            colors.recycle();
        }
        return returnColor;
    }

    @Override
    public int getItemCount() {
        return kids.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView tvTime, tvName;
        public LinearLayout eventHolder;
        public View view;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageView);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            eventHolder = (LinearLayout)itemView.findViewById(R.id.eventHolder);
            view = (View)itemView.findViewById(R.id.separatorView);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder{

        public TextView tvKidName;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tvKidName = (TextView)itemView.findViewById(R.id.tvKidName);
        }
    }
}
