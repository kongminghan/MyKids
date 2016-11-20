package com.workshop2.mykids;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.graphics.Rect;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.RadialPickerLayout;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.workshop2.mykids.adapter.HospitalAdapter;
import com.workshop2.mykids.model.Hospital;
import com.workshop2.mykids.model.Vaccine;
import com.workshop2.mykids.other.Receiver;
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
    private Calendar cal, cal2;
    private String sName, sDate, sID, sTime, message;
    private DatePickerDialog dpd;
    private TimePickerDialog timePickerDialog;
    private Vaccine vaccine;
    private BottomSheetBehavior bottomSheetBehavior;
    private HospitalAdapter adapter;
    private ArrayList<Hospital> hospitals;
    private int index=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule_detail);
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        setSupportActionBar(binding.toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_action_close);

        //data initialization
        sName = getIntent().getStringExtra("sName");
        sDate = getIntent().getStringExtra("sDate");
        sTime = getIntent().getStringExtra("sTime");
        sID = getIntent().getStringExtra("sID");
        binding.txtTitle.setText(sName + " Vaccination");
//        getSupportActionBar().setTitle(sName);

//        bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheet);
//        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

//        hospitals = getHospitals();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowmanager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        windowmanager.getDefaultDisplay().getMetrics(displayMetrics);
        int deviceWidth = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

//        adapter = new HospitalAdapter(ScheduleDetailActivity.this, hospitals, deviceWidth);
//        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 1, GridLayoutManager.HORIZONTAL, false);
//        binding.rvHospital.setLayoutManager(layoutManager);
//        binding.rvHospital.setHasFixedSize(true);
//        binding.rvHospital.addItemDecoration(new ScheduleDetailActivity.GridSpacingItemDecoration(1, dpToPx(10), true));
//        binding.rvHospital.setItemAnimator(new DefaultItemAnimator());
//        binding.rvHospital.setAdapter(adapter);

        binding.txtReveal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VaccineBottomSheetDialog bottomSheetDialog = VaccineBottomSheetDialog.getInstance(sName);
                bottomSheetDialog.show(getSupportFragmentManager(), "Custom Bottom Sheet");
            }
        });

//        binding.txtReveal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                bottomSheetBehavior.setPeekHeight(height*2/5);
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//            }
//        });

//        binding.layoutBottom.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
//                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                }
//            }
//        });

//        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
//            @Override
//            public void onStateChanged(@NonNull View bottomSheet, int newState) {
//                if(newState == BottomSheetBehavior.STATE_COLLAPSED){
//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)binding.bottomSheetL.getLayoutParams();
//                    params.setMargins(0,26,0,0);
//                    binding.bottomSheetL.setLayoutParams(params);
//                    binding.vImg.setVisibility(View.GONE);
//                }
//                else if(newState == BottomSheetBehavior.STATE_EXPANDED){
//                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)binding.bottomSheetL.getLayoutParams();
//                    params.setMargins(0,116,0,0);
//                    binding.bottomSheetL.setLayoutParams(params);
//                    binding.vImg.setVisibility(View.VISIBLE);
//                }
//                else
//                    getWindow().getDecorView().setSystemUiVisibility(0);
//
//            }
//
//            @Override
//            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                cal = Calendar.getInstance();
                SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");
                Date date = null;
                try {
                    date = se.parse(sDate + " "+ sTime);
                    cal.setTime(date);
                    cal2 = cal;
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy");
                String tempDate= null;
                try {
                    Date date2 = sim.parse(sDate);
                    sim.applyPattern("EEE d MMM");
                    tempDate = sim.format(date2).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                final String finalTempDate = tempDate;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.txtDate.setText(finalTempDate);
                        binding.txtTime.setText(sTime);
                    }
                });
            }
        }).start();

        message = "Notification will be triggered 2 minutes before.";
        binding.txtNotify.setItems("2 minutes before", "30 minutes before", "1 day before", "2 days before", "None");
        binding.txtNotify.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
//                setAlarm(position);
            }
        });

        binding.txtDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Calendar now = Calendar.getInstance();
                dpd = DatePickerDialog.newInstance(
                        ScheduleDetailActivity.this,
                        cal2.get(Calendar.YEAR),
                        cal2.get(Calendar.MONTH),
                        cal2.get(Calendar.DAY_OF_MONTH)
                );
                dpd.setAccentColor(getResources().getColor(R.color.schedule));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        binding.txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Calendar c = Calendar.getInstance();
                timePickerDialog = TimePickerDialog.newInstance(
                        ScheduleDetailActivity.this,
                        cal2.get(Calendar.HOUR),
                        cal2.get(Calendar.MINUTE),
                        false
                );
                timePickerDialog.setAccentColor(getResources().getColor(R.color.schedule));
                timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
            }
        });

        if(getIntent().getBooleanExtra("sStatus", false)){
            binding.takenCheckbox.setChecked(true);
            binding.toolbar.setBackground(getDrawable(R.drawable.done));
        }

        binding.takenCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setStatus(isChecked);
                if(isChecked){
                    binding.toolbar.setBackground(getDrawable(R.drawable.done));
                }
                else{
                    binding.toolbar.setBackgroundColor(getResources().getColor(R.color.schedule));
                }
            }
        });

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyDate() && verifyTime()){
                    setAlarm(binding.txtNotify.getSelectedIndex());
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleDetailActivity.this);
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
                }
            }
        });

//        setVaccine();
//
//        binding.touchOutside.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//            }
//        });
    }

    public void setAlarm(final int position){
        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy hh:mm a");

        Intent myIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
        myIntent.setAction("android.media.action.DISPLAY_NOTIFICATION");
        myIntent.putExtra("title", sName);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ScheduleDetailActivity.this, Integer.parseInt(sID)+(int)cal.getTimeInMillis(), myIntent,0);
        AlarmManager alarmManager = (AlarmManager)getApplicationContext().getSystemService(Context.ALARM_SERVICE);
         if (position == 0) {
             cal2.add(Calendar.MINUTE, -2);
             message = "Notification will be triggered on "+se.format(cal2.getTime());
         } else if (position == 1) {
             cal2.add(Calendar.MINUTE, -30);
             message = "Notification will be triggered on "+se.format(cal2.getTime());
         } else if (position == 2) {
             cal2.add(Calendar.DAY_OF_MONTH, -1);
             message = "Notification will be triggered on "+se.format(cal2.getTime());
         } else if (position == 3) {
             cal2.add(Calendar.DAY_OF_MONTH, -2);
             message = "Notification will be triggered on "+se.format(cal2.getTime());
         }else if (position == 4){
             cal2.add(Calendar.SECOND, 0);
             message = "Notification will be triggered on "+se.format(cal2.getTime());
         }
//        Calendar calendar = Calendar.getInstance();
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal2.getTimeInMillis(), pendingIntent); ;
        Toast.makeText(ScheduleDetailActivity.this, message, Toast.LENGTH_LONG).show();

//        Log.d("sda", alarmManager.getNextAlarmClock().toString());
//                        Snackbar snackbar = Snackbar
//                                .make(binding.txtNotify, message, Snackbar.LENGTH_LONG);
//                        snackbar.show();
        updateSchedule();
    }

    @Override
    public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int second) {
        cal2.set(Calendar.HOUR_OF_DAY, hourOfDay);
        cal2.set(Calendar.MINUTE, minute);
        sTime = hourOfDay+":"+minute+":"+second;

//        if(verifyTime()){
            DateFormat se = new SimpleDateFormat("hh:mm:ss");
            try {
                Date date = se.parse(sTime);
                sTime = new SimpleDateFormat("hh:mm a").format(date);
                binding.txtTime.setText(sTime);
//                setAlarm(index);

            } catch (ParseException e) {
                e.printStackTrace();
            }
//        }
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        sDate = dayOfMonth+"-"+(monthOfYear+1)+"-"+year;
        cal2.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        cal2.set(Calendar.YEAR, year);
        cal2.set(Calendar.MONTH, monthOfYear);

//        if(verifyDate()){
            SimpleDateFormat sim = new SimpleDateFormat("dd-MM-yyyy");
            String tempDate= null;
            try {
                Date date2 = sim.parse(sDate);
                sim.applyPattern("EEE d MMM");
                tempDate = sim.format(date2).toString();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            binding.txtDate.setText(tempDate);
//            setAlarm(binding.txtNotify.getSelectedIndex());
//            timePickerDialog.setAccentColor(getResources().getColor(R.color.schedule));
//            timePickerDialog.show(getFragmentManager(), "Timepickerdialog");
//        }
    }

    private void updateSchedule(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid());

                Map<String, Object> s = new HashMap<>();
                s.put("s_date", sDate);
                s.put("s_time", sTime);
                s.put("s_id", sID);
                s.put("s_name", sName);
                s.put("type", 10);

                if (user.getUid() != null) {
                    DatabaseReference scheduleRef = mRef.child("kid").child(getIntent().getStringExtra("kID")).child("schedule").child(""+(Integer.parseInt(sID)-1));
                    scheduleRef.keepSynced(true);
                    scheduleRef.updateChildren(s);
                }
                else
                    Log.d("FB", "failed to get current user");
            }
        }).start();
    }

    private boolean verifyDate(){
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        if(today.after(cal2)){
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setTitle("Invalid date");
//            builder.setMessage("The date set is not a valid date. Please set a valid date instead. ");
//            builder.setNegativeButton("Dismiss", new DialogInterface.OnClickListener() {
//
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    dpd.show(getFragmentManager(), "Datepickerdialog");
//                }
//            });
//
//            AlertDialog alert = builder.create();
//            alert.show();
            return false;
        }
        else
            return true;
    }

    private boolean verifyTime(){
        Calendar now = Calendar.getInstance();
        if(now.after(cal2)){
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

    @Override
    public void onBackPressed() {
//        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//        }
//        else if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
//            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
//        }else {
            super.onBackPressed();
//        }
    }

    private ArrayList<Hospital> getHospitals(){
        final ArrayList<Hospital> hospitals = new ArrayList<>();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("hospitals");
        mRef.keepSynced(true);
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
            public void onCancelled(DatabaseError firebaseError) {
            }
        });
        return hospitals;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)
                outRect.top = spacing;
//                if (position < spanCount) { // top edge
//                    outRect.top = spacing;
//                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void setupWindowAnimations() {
        Transition exitTrans = new Explode();
        getWindow().setExitTransition(exitTrans);

        Transition reenterTrans = new Slide();
        getWindow().setReenterTransition(reenterTrans);
    }

    private void setStatus(final boolean isChecked){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isChecked){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    DatabaseReference mRef =FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());

                    if (user.getUid() != null) {
                        DatabaseReference statusRef = mRef.child("kid")
                                .child(getIntent().getStringExtra("kID"))
                                .child("schedule")
                                .child(""+(Integer.parseInt(sID)-1))
                                .child("s_status");
                        statusRef.keepSynced(true);
                        statusRef.setValue(true);
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
                    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser()
                                    .getUid());

                    if (user.getUid() != null) {
                        DatabaseReference statusRef = mRef.child("kid")
                                .child(getIntent().getStringExtra("kID"))
                                .child("schedule")
                                .child(""+(Integer.parseInt(sID)-1))
                                .child("s_status");
                        statusRef.keepSynced(true);
                        statusRef.setValue(false);
                    }
                }
            }
        }).start();
    }
}
