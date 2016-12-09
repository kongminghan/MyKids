package com.workshop2.mykids.adapter;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.google.android.gms.vision.text.Line;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.workshop2.mykids.EventFragment;
import com.workshop2.mykids.R;
import com.workshop2.mykids.ScheduleDetailActivity;
import com.workshop2.mykids.VaccineBottomSheetDialog;
import com.workshop2.mykids.model.Event;
import com.workshop2.mykids.model.Schedule;
import com.workshop2.mykids.other.CircleTransform;
import com.workshop2.mykids.other.Receiver;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.workshop2.mykids.model.Event.EVENT;
import static com.workshop2.mykids.model.Event.TITLE;

/**
 * Created by MingHan on 20/11/2016.
 */

public class EventAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private ArrayList<Event> kids;
    private int mExpandedPosition = -1;
    private RecyclerView recyclerView;

    public EventAdapter(Context context, ArrayList<Event> kids, RecyclerView recyclerView){
        this.context = context;
        this.kids = kids;
        this.recyclerView = recyclerView;
        JodaTimeAndroid.init(context);
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
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final Event event = kids.get(position);
        switch (event.getType()){
            case TITLE:
                ((TitleViewHolder)holder).tvKidName.setText(event.getKid_name());
                ((TitleViewHolder)holder).tvKidDate.setText(" ("+event.getKid_date()+")");
                break;

            case EVENT:
                final boolean isExpanded = position==mExpandedPosition;
                final String[] sDate = new String[1];
                final String[] sTime = new String[1];
                final Calendar c = Calendar.getInstance();
                final Calendar cal2 = Calendar.getInstance();
                final SimpleDateFormat[] se = {new SimpleDateFormat("dd-MM-yyyy hh:mm a")};
                Date date = null;
                try {
                    date = se[0].parse(event.getS_date()+" "+event.getS_time());
                    c.setTime(date);
                    ((MyViewHolder)holder).tvTime.setText(new SimpleDateFormat("EEE, d MMM yyyy hh:mm a").format(c.getTime()));
                    ((MyViewHolder)holder).txtDate.setText(new SimpleDateFormat("dd-MMM-yyyy").format(c.getTime()));
                    ((MyViewHolder)holder).txtTime.setText(new SimpleDateFormat("hh:mm a").format(c.getTime()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                ((MyViewHolder)holder).tvName.setText(event.getKid_name()+"'s "+event.getS_name()+" vaccination");

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        se[0] = new SimpleDateFormat("dd-MM-yyyy");
                        Date d = null;
                        try {
                            d = se[0].parse(event.getS_date());
                            LocalDate vaccineDate = new LocalDate(d);
                            d = se[0].parse(event.getKid_date());
                            LocalDate birthDate = new LocalDate(d);
                            Period period = new Period(birthDate, vaccineDate, PeriodType.yearMonthDay());
                            String s = "";
                            if(period.getYears()!=0) {
                                s = String.valueOf(period.getYears()) + " year(s)  "
                                        + String.valueOf(period.getMonths()) + " month(s)";
                            }
                            else{
                                s = String.valueOf(period.getMonths()) + " month(s)";
                            }
                            final String finalS = s;
                            ((Activity)context).runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ((MyViewHolder)holder).tvAge.setText(finalS);
                                }
                            });
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int bgColor = getMatColor("600");
                        final TextDrawable drawable1 = TextDrawable.builder()
                                .buildRound(event.getS_name().substring(0,1), bgColor); // radius in px
                        ((Activity)context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((MyViewHolder)holder).imageView.setImageDrawable(drawable1);
                            }
                        });
                    }
                }).start();

                ((MyViewHolder)holder).view.setVisibility(View.VISIBLE);
                ((MyViewHolder)holder).action.setVisibility(isExpanded? View.VISIBLE:View.GONE);
                ((MyViewHolder)holder).eventHolder.setActivated(isExpanded);
                ((MyViewHolder)holder).eventHolder.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mExpandedPosition = isExpanded? -1:position;
                        TransitionManager.beginDelayedTransition(recyclerView);
                        notifyDataSetChanged();
//                        Intent intent = new Intent(context, ScheduleDetailActivity.class);
//                        intent.putExtra("sName", event.getS_name());
//                        intent.putExtra("sDate", event.getS_date());
//                        intent.putExtra("sID", event.getS_id());
//                        intent.putExtra("sTime", event.getS_time());
//                        intent.putExtra("sStatus", event.getS_status());
//                        intent.putExtra("kID", event.getKid_id());
//                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity)context, ((EventAdapter.MyViewHolder) holder).eventHolder, "tScheduleHolder");
//                        context.startActivity(intent, optionsCompat.toBundle());
//                        context.startActivity(intent);
                    }
                });

                ((MyViewHolder)holder).txtDate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                                        sDate[0] = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
                                        cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                        cal2.set(Calendar.YEAR, year);
                                        cal2.set(Calendar.MONTH, monthOfYear);

                                        SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy");
                                        String tempDate= null;
                                        try {
                                            Date date2 = sim.parse(sDate[0]);
                                            tempDate = sim.format(date2).toString();
                                            ((MyViewHolder)holder).txtDate.setText(tempDate);
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                c.get(Calendar.YEAR),
                                c.get(Calendar.MONTH),
                                c.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setAccentColor(context.getResources().getColor(R.color.schedule));
                        dpd.show(((FragmentActivity)context).getFragmentManager(), "Datepickerdialog");
                    }
                });

                ((MyViewHolder)holder).txtTime.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TimePickerDialog timePickerDialog = TimePickerDialog.newInstance(
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
                                        cal2.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                        cal2.set(Calendar.MINUTE, minute);
                                        sTime[0] = hourOfDay+":"+minute+":"+second;
                                        DateFormat se = new SimpleDateFormat("hh:mm:ss");
                                        try {
                                            Date date = se.parse(sTime[0]);
                                            sTime[0] = new SimpleDateFormat("hh:mm a").format(date);
                                            ((MyViewHolder)holder).txtTime.setText(sTime[0]);

                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                },
                                c.get(Calendar.HOUR),
                                c.get(Calendar.MINUTE),
                                false
                        );
                        timePickerDialog.setAccentColor(context.getResources().getColor(R.color.schedule));
                        timePickerDialog.show(((FragmentActivity)context).getFragmentManager(), "Timepickerdialog");
                    }
                });

                ((MyViewHolder)holder).btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(verifyDate(cal2) && verifyTime(cal2)){
                            setAlarm(event.getS_name(), cal2, event.getS_id());
                            updateSchedule(sDate[0], sTime[0], event.getS_id(), event.getS_name(), event.getKid_id());
                        }
                        mExpandedPosition = isExpanded? -1:position;
                        TransitionManager.beginDelayedTransition(recyclerView);
                        notifyDataSetChanged();
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
        public TextView tvTime, tvName, tvAge, txtTime, txtDate;
        public LinearLayout eventHolder;
        public View view;
        public LinearLayout action;
        public Button btnSave;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView)itemView.findViewById(R.id.imageView);
            tvTime = (TextView)itemView.findViewById(R.id.tvTime);
            tvName = (TextView)itemView.findViewById(R.id.tvName);
            tvAge = (TextView)itemView.findViewById(R.id.tvAge);
            eventHolder = (LinearLayout)itemView.findViewById(R.id.eventHolder);
            view = (View)itemView.findViewById(R.id.separatorView);
            action = (LinearLayout)itemView.findViewById(R.id.action);
            txtDate = (TextView)itemView.findViewById(R.id.txtDate);
            txtTime = (TextView)itemView.findViewById(R.id.txtTime);
            btnSave = (Button)itemView.findViewById(R.id.btnSave);
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder{

        public TextView tvKidName, tvKidDate;

        public TitleViewHolder(View itemView) {
            super(itemView);
            tvKidName = (TextView)itemView.findViewById(R.id.tvKidName);
            tvKidDate = (TextView) itemView.findViewById(R.id.date);
        }
    }

    private boolean verifyDate(Calendar cal2){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if(today.after(cal2)){
            return false;
        }
        else
            return true;
    }

    private boolean verifyTime(Calendar cal2){
        Calendar now = Calendar.getInstance();
        if(now.after(cal2)){
            return false;
        }
        else
            return true;
    }

    private void updateSchedule(final String sDate, final String sTime, final String sID, final String sName, final String kid){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map<String, Object> s = new HashMap<>();
                s.put("s_date", sDate);
                s.put("s_time", sTime);
                s.put("s_id", sID);
                s.put("s_name", sName);
                s.put("type", 10);

                if (user.getUid() != null) {
                    DatabaseReference scheduleRef = mRef.child("kid").child(kid).child("schedule").child(""+(Integer.parseInt(sID)-1));
                    scheduleRef.keepSynced(true);
                    scheduleRef.updateChildren(s);
                }
                else
                    Log.d("FB", "failed to get current user");
            }
        }).start();
    }

    public void setAlarm(String sName, Calendar cal2, String sID){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Intent myIntent = new Intent(context, Receiver.class);
//        myIntent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        myIntent.putExtra("title", sName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, Integer.parseInt(sID)+(int)cal2.getTimeInMillis(), myIntent,0);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent); ;
        Toast.makeText(context, "Notification will be triggered on "+se.format(cal2.getTime()), Toast.LENGTH_LONG).show();
    }
}
