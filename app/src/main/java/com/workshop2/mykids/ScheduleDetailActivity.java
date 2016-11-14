package com.workshop2.mykids;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import com.amulyakhare.textdrawable.TextDrawable;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.workshop2.mykids.Adapter.HospitalAdapter;
import com.workshop2.mykids.Model.Hospital;
import com.workshop2.mykids.Model.Vaccine;
import com.workshop2.mykids.Other.Receiver;
import com.workshop2.mykids.databinding.ActivityScheduleDetailBinding;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScheduleDetailActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,DatePickerDialog.OnDateSetListener {

    //eliminate findViewById
    ActivityScheduleDetailBinding binding;
    private Calendar cal;
    private String sName, sDate, sID, sTime, message;
    private DatePickerDialog dpd;
    private TimePickerDialog timePickerDialog;
    private Vaccine vaccine;
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView recyclerView;
    private HospitalAdapter adapter;
    private ArrayList<Hospital> hospitals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_detail);
//        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);

        //data binding
        sName = getIntent().getStringExtra("sName");
        sDate = getIntent().getStringExtra("sDate");
        sTime = getIntent().getStringExtra("sTime");
        sID = getIntent().getStringExtra("sID");

        binding.txtTitle.setText(sName);
//        getSupportActionBar().setTitle(sName);

        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        hospitals = getHospitals();
        adapter = new HospitalAdapter(ScheduleDetailActivity.this, hospitals);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        binding.rvHospital.setLayoutManager(layoutManager);
        binding.rvHospital.setHasFixedSize(true);
        binding.rvHospital.setAdapter(adapter);

        binding.txtReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setPeekHeight(620);
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });

        binding.layoutBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)binding.bottomSheetL.getLayoutParams();
                    params.setMargins(0,26,0,0);
                    binding.bottomSheetL.setLayoutParams(params);
                    binding.vImg.setVisibility(View.GONE);
                }
                else if(newState == BottomSheetBehavior.STATE_EXPANDED){
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)binding.bottomSheetL.getLayoutParams();
                    params.setMargins(0,116,0,0);
                    binding.bottomSheetL.setLayoutParams(params);
                    binding.vImg.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        binding.txtDate.setText(sDate);
        binding.txtTime.setText(sTime);

        message = "Notification will be triggered 2 minutes before.";
        binding.txtNotify.setItems("2 minutes before", "30 minutes before", "1 day before", "2 days before", "None");
        binding.txtNotify.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                setAlarm(position);
            }
        });

        cal = Calendar.getInstance();
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Date date = null;
        try {
            date = se.parse(sDate + " "+ sTime);
            cal.setTime(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        binding.txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        ScheduleDetailActivity.this,
                        now.get(Calendar.YEAR),
                        now.get(Calendar.MONTH),
                        now.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.schedule));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        binding.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                timePickerDialog = TimePickerDialog.newInstance(
                        ScheduleDetailActivity.this,
                        c.get(Calendar.HOUR),
                        c.get(Calendar.MINUTE),
                        false
                );
                timePickerDialog.setAccentColor(getResources().getColor(R.color.schedule));
                timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        if(getIntent().getBooleanExtra("sStatus", false)){
            binding.takenCheckbox.setChecked(true);
            binding.appBarMain.setBackground(getDrawable(R.drawable.done));
        }

        binding.takenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/User")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());

                    if (user.getUid() != null) {
                        mRef.child("kid")
                                .child(getIntent().getStringExtra("kID"))
                                .child("schedule")
                                .child(""+(Integer.parseInt(sID)-1))
                                .child("s_status")
                                .setValue(true);
                    }

                    Intent myIntent = new Intent(ScheduleDetailActivity.this, Receiver.class);
                    myIntent.putExtra("title", sName);
                    PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleDetailActivity.this, Integer.parseInt(sID), myIntent,0);
                    AlarmManager alarmManager = (AlarmManager)ScheduleDetailActivity.this.getSystemService(ALARM_SERVICE);
                    try{
                        alarmManager.cancel(pendingIntent);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/User")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());

                    if (user.getUid() != null) {
                        mRef.child("kid")
                                .child(getIntent().getStringExtra("kID"))
                                .child("schedule")
                                .child(""+(Integer.parseInt(sID)-1))
                                .child("s_status")
                                .setValue(false);
                    }
                }
            }
        });
        setVaccine();

        binding.touchOutside.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });
    }

    public void setAlarm(int position){
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
        Intent myIntent = new Intent(this, Receiver.class);
        myIntent.putExtra("title", sName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(sID), myIntent,0);
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);

        try{
            alarmManager.cancel(pendingIntent);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (position == 0) {
            cal.add(Calendar.MINUTE, -2);
            message = "Notification will be triggered on "+se.format(cal.getTime());
        } else if (position == 1) {
            cal.add(Calendar.MINUTE, -30);
            message = "Notification will be triggered on "+se.format(cal.getTime());
        } else if (position == 2) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
            message = "Notification will be triggered on "+se.format(cal.getTime());
        } else if (position == 3) {
            cal.add(Calendar.DAY_OF_MONTH, -2);
            message = "Notification will be triggered on "+se.format(cal.getTime());
        }else if (position == 4){
            cal.add(Calendar.SECOND, 0);
            message = "Notification will be triggered on "+se.format(cal.getTime());
        }

        updateSchedule();

        alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent) ;
        Snackbar snackbar = Snackbar
                .make(binding.activityScheduleDetail, message, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        cal.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal.set(Calendar.MINUTE, minute);
        sTime = hourOfDay+":"+minute+":"+second;

        if(verifyTime()){
            DateFormat se = new SimpleDateFormat("hh:mm:ss");
            try {
                Date date = se.parse(sTime);
                sTime = new SimpleDateFormat("hh:mm a").format(date);
                binding.txtTime.setText(sTime);
                setAlarm(binding.txtNotify.getSelectedIndex());

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        sDate = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, monthOfYear);

        if(verifyDate()){
            Calendar c = Calendar.getInstance();
            timePickerDialog = TimePickerDialog.newInstance(
                    ScheduleDetailActivity.this,
                    c.get(Calendar.HOUR),
                    c.get(Calendar.MINUTE),
                    false
            );
            binding.txtDate.setText(sDate);
//            setAlarm(binding.txtNotify.getSelectedIndex());
            timePickerDialog.setAccentColor(getResources().getColor(R.color.schedule));
            timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
        }
    }

    private void updateSchedule(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        Map<String, Object> s = new HashMap<>();
        s.put("s_date", sDate);
        s.put("s_time", sTime);
        s.put("s_id", sID);
        s.put("s_name", sName);
        s.put("type", 10);

        if (user.getUid() != null) {
            mRef.child("kid").child(getIntent().getStringExtra("kID")).child("schedule").child(""+(Integer.parseInt(sID)-1)).updateChildren(s);
        }
        else
            Log.d("FB", "failed to get current user");
    }

    private boolean verifyDate(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if(today.after(cal)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid date");
            builder.setMessage("The date set is not a valid date. Please set a valid date instead. ");
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    dpd.show(getFragmentManager(), "Datepickerdialog");
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        else
            return true;
    }

    private boolean verifyTime(){
        Calendar now = Calendar.getInstance();
        if(now.after(cal)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Invalid time");
            builder.setMessage("The time set is not a valid time. Please set a valid time instead. ");
            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
                }
            });

            AlertDialog alert = builder.create();
            alert.show();
            return false;
        }
        else
            return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // close this activity as oppose to navigating up
        return false;
    }

    private void setVaccine(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/vaccine");

        if (user.getUid() != null) {
            mRef.orderByChild("vaccine_name").equalTo(sName).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        vaccine = postSnapshot.getValue(Vaccine.class);
                    }
//                    Toast.makeText(ScheduleDetailActivity.this, vaccine.getVaccine_func(), Toast.LENGTH_LONG).show();
                    binding.vTitle.setText(sName);
                    binding.vDes.setText(vaccine.getVaccine_func());
                    binding.vDis.setText(vaccine.getVaccine_dis());
                    binding.vSym.setText(vaccine.getVaccine_sym());

                    Glide.with(ScheduleDetailActivity.this).load(vaccine.getVaccine_image()).into(binding.vImg);

                    Glide.with(ScheduleDetailActivity.this)
                            .load(vaccine.getVaccine_image())
                            .asBitmap()
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                                    Palette.generateAsync(resource, new Palette.PaletteAsyncListener() {
                                        public void onGenerated(Palette palette) {
                                            int bgColor = palette.getVibrantColor(ScheduleDetailActivity.this.getResources().getColor(R.color.white));
                                            TextDrawable drawable1 = TextDrawable.builder()
                                                    .buildRound(sName.substring(0,1), bgColor); // radius in px
                                            binding.vLetterView.setImageDrawable(drawable1);
                                        }
                                    });
                                    binding.vImg.setImageBitmap(resource);
                                }
                            });
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        }else {
            super.onBackPressed();
        }
    }

    private ArrayList<Hospital> getHospitals(){
        final ArrayList<Hospital> hospitals = new ArrayList<>();
        Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/").child("hospitals");
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hospitals.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Hospital hospital = postSnapshot.getValue(Hospital.class);
                    hospitals.add(hospital);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        return hospitals;
    }
}
