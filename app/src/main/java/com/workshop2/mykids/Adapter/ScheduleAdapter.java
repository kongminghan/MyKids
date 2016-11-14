package com.workshop2.mykids.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.fmsirvent.ParallaxEverywhere.PEWImageView;
import com.workshop2.mykids.Model.Schedule;
import com.workshop2.mykids.R;
import com.workshop2.mykids.ScheduleDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.workshop2.mykids.Model.Schedule.CALENDER_TYPE;
import static com.workshop2.mykids.Model.Schedule.EVENT_TYPE;


/**
 * Created by MingHan on 26/10/2016.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public ArrayList<Schedule> schedules;
    public Context context;

    public  ScheduleAdapter (Context context, ArrayList<Schedule> schedules){
        this.schedules = schedules;
        this.context = context;
    }

    @Override
    public int getItemViewType(int position) {
        if(schedules != null){
            Schedule s = schedules.get(position);
            return s.getType();
        }
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType){
            case CALENDER_TYPE:
                itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_calendar, parent, false);
                return new CalendarViewHolder(itemView);
            case EVENT_TYPE:
                itemView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.card_schedule, parent, false);
                return new MyViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        Schedule s = schedules.get(position);
        final int index = position;
        switch (s.getType()){
            case CALENDER_TYPE:
                SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy");
                Calendar c = Calendar.getInstance();
                Date date = null;
                try {
                    date = se.parse(s.getS_date());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                c.setTime(date);
                int m = c.get(Calendar.MONTH);
                if(m==0){
                    Glide.with(context).load(R.drawable.january).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==1){
                    Glide.with(context).load(R.drawable.february).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==2){
                    Glide.with(context).load(R.drawable.march).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==3){
                    Glide.with(context).load(R.drawable.april).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==4){
                    Glide.with(context).load(R.drawable.may).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==5){
                    Glide.with(context).load(R.drawable.june).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==6){
                    Glide.with(context).load(R.drawable.july).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==7){
                    Glide.with(context).load(R.drawable.august).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==8){
                    Glide.with(context).load(R.drawable.september).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==9){
                    Glide.with(context).load(R.drawable.october).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==10){
                    Glide.with(context).load(R.drawable.november).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                else if(m==11){
                    Glide.with(context).load(R.drawable.december).asBitmap().into(((CalendarViewHolder) holder).imageView);
                    ((CalendarViewHolder) holder).monthYear.setText(new SimpleDateFormat("MMMM").format(c.getTime())+" "+ c.get(Calendar.YEAR));
                }
                break;

            case EVENT_TYPE:
                try {
                    se = new SimpleDateFormat("dd-MM-yyyy");
                    c = Calendar.getInstance();
                    date = se.parse(s.getS_date());
                    c.setTime(date);

//                    if(position==1){
                    ((MyViewHolder) holder).title.setText(s.getS_name());
                    ((MyViewHolder) holder).day.setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
                    se.applyPattern("MMM");
                    ((MyViewHolder) holder).day_word.setText(se.format(date).toString());

                    if(s.getS_status()==true){
//                        Toast.makeText(context, s.getS_name(), Toast.LENGTH_LONG).show();
                        ((MyViewHolder) holder).relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.schedule_checked));
                        ((MyViewHolder) holder).img.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_done_black_24dp));
                    }else{
                        ((MyViewHolder) holder).relativeLayout.setBackgroundColor(context.getResources().getColor(R.color.schedule));
                        ((MyViewHolder) holder).img.setImageResource(android.R.color.transparent);;
                    }


//                    }
//                    else{
//                        if(s.getType() == 11 && schedules.get(position-1).getType() == 11){
//
//                        }
//                        if(s.getS_date().equals(schedules.get(position-1).getS_date())){
//                            ((MyViewHolder) holder).title.setText(s.getS_name());
//                            ((MyViewHolder) holder).day.setText("");
//                            ((MyViewHolder) holder).day_word.setText("");
//                        }else{
//                            ((MyViewHolder) holder).title.setText(s.getS_name());
//                            ((MyViewHolder) holder).day.setText(Integer.toString(c.get(Calendar.DAY_OF_MONTH)));
//                            se.applyPattern("MMM");
//                            ((MyViewHolder) holder).day_word.setText(se.format(date).toString());
//                        }
//                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((MyViewHolder) holder).cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ScheduleDetailActivity.class);
                        intent.putExtra("sName", schedules.get(position).getS_name());
                        intent.putExtra("sDate", schedules.get(position).getS_date());
                        intent.putExtra("sID", schedules.get(position).getS_id());
                        intent.putExtra("sTime", schedules.get(position).getS_time());
                        intent.putExtra("sStatus", schedules.get(position).getS_status());
                        intent.putExtra("kID", ((Activity) context).getIntent().getStringExtra("kid"));
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, ((MyViewHolder) holder).cardView, "tScheduleHolder");
                        context.startActivity(intent, optionsCompat.toBundle());
                    }
                });
                break;
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView title, day, day_word;
        private CardView cardView;
        private ImageView img;
        private RelativeLayout relativeLayout;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            day = (TextView)itemView.findViewById(R.id.day);
            day_word = (TextView)itemView.findViewById(R.id.day_word);
            cardView = (CardView)itemView.findViewById(R.id.card_view);
            img = (ImageView)itemView.findViewById(R.id.status);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.rlSchedule);
        }
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder{
        private PEWImageView imageView;
        private TextView monthYear;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            imageView = (PEWImageView)itemView.findViewById(R.id.monthImage);
            monthYear = (TextView)itemView.findViewById(R.id.tvMonthYear);
        }
    }
}

