package com.workshop2.mykids.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.workshop2.mykids.Model.Schedule;
import com.workshop2.mykids.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by MingHan on 26/10/2016.
 */

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.MyViewHolder>{

    public ArrayList<Schedule> schedules;
    public Context context;

    public  ScheduleAdapter (Context context, ArrayList<Schedule> schedules){
        this.schedules = schedules;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_schedule, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Schedule s = schedules.get(position);
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            Date date = se.parse(s.getS_date());
            c.setTime(date);
            holder.day.setText(Integer.toString(c.get(Calendar.DAY_OF_WEEK)));
            se.applyPattern("EEE");
//            System.out.println(se.format(date).toString());
            holder.title.setText(s.getS_name());
            holder.day_word.setText(se.format(date).toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView title, day, day_word;
        public MyViewHolder(View itemView) {
            super(itemView);

            title = (TextView)itemView.findViewById(R.id.title);
            day = (TextView)itemView.findViewById(R.id.day);
            day_word = (TextView)itemView.findViewById(R.id.day_word);
        }
    }
}
