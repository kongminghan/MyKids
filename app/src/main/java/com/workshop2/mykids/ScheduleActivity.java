package com.workshop2.mykids;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Explode;
import android.transition.Slide;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.ScheduleAdapter;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.model.Schedule;
import com.workshop2.mykids.databinding.ActivityScheduleBinding;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduleActivity extends AppCompatActivity {
    private ArrayList<Schedule> scheduleList;
    private Kid kid;
    private ScheduleAdapter scheduleAdapter;
    ActivityScheduleBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);
//        setupTransition();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDarkKid));
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra("kname"));
        binding.toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//        binding.collapsingToolbar.setTitleEnabled(false);


//        Intent intent = getIntent();
//        scheduleList = (ArrayList<Schedule>) intent.getSerializableExtra("schedule");
//        Collections.sort(scheduleList, new Comparator<Schedule>() {
//            @Override
//            public int compare(Schedule c0, Schedule c1) {
//                try {
//                    return c0.compare().compareTo(c1.compare());
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                return 0;
//            }
//        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){

            try {
                scheduleList = getScheduleList();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            scheduleAdapter = new ScheduleAdapter(ScheduleActivity.this, scheduleList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            mLayoutManager.scrollToPosition(10);
            binding.rvSchedule.setLayoutManager(mLayoutManager);
            binding.rvSchedule.setItemAnimator(new DefaultItemAnimator());
            binding.rvSchedule.setHasFixedSize(true);
            binding.rvSchedule.setAdapter(scheduleAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                supportFinishAfterTransition();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    public ArrayList<Schedule> processSchedules() throws ParseException {
//        ArrayList<Schedule> s = new ArrayList<Schedule>();
//        SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy");
//        Calendar c = Calendar.getInstance();
//        Calendar cc = Calendar.getInstance();
//
//        s.add(new Schedule(scheduleList.get(0).getS_date(),11));
//
//        for(int i=1; i<scheduleList.size(); i++){
//            Date date = se.parse(scheduleList.get(i-1).getS_date());
//            Date date1 = se.parse(scheduleList.get(i).getS_date());
//            c.setTime(date);
//            cc.setTime(date1);
//
//            if(c.get(Calendar.MONTH) != cc.get(Calendar.MONTH) || c.get(Calendar.YEAR) != cc.get(Calendar.YEAR)){
//                s.add(scheduleList.get(i-1));
//                s.add(new Schedule(scheduleList.get(i).getS_date(), 11));
//            }
//            else
//                s.add(scheduleList.get(i-1));
//        }
//        s.add(scheduleList.get(scheduleList.size()-1));
//        return s;
//    }

    private  ArrayList<Schedule> getScheduleList() throws ParseException {
        final ArrayList<Schedule> s = new ArrayList<>();
        final ArrayList<Schedule> ss = new ArrayList<>();

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userRef = mRef.child("User").
                child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                child("kid").
                child(getIntent().getStringExtra("kid")).
                child("schedule");

        userRef.keepSynced(true);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ss.clear();
                s.clear();
                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                        Schedule schedule = postSnapshot.getValue(Schedule.class);
                        ss.add(schedule);
                    }
                    SimpleDateFormat se = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar c = Calendar.getInstance();
                    Calendar cc = Calendar.getInstance();

                    s.add(new Schedule(ss.get(0).getS_date(),11));

                    for(int i=1; i<ss.size(); i++){
                        Date date = null;
                        Date date1 = null;
                        try {
                            date = se.parse(ss.get(i-1).getS_date());
                            date1 = se.parse(ss.get(i).getS_date());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        c.setTime(date);
                        cc.setTime(date1);

                        if(c.get(Calendar.MONTH) != cc.get(Calendar.MONTH) || c.get(Calendar.YEAR) != cc.get(Calendar.YEAR)){
                            s.add(ss.get(i-1));
                            s.add(new Schedule(ss.get(i).getS_date(), 11));
                        }
                        else
                            s.add(ss.get(i-1));
                    }
                    s.add(ss.get(ss.size()-1));

                    scheduleAdapter.notifyDataSetChanged();
                    System.out.println("Schedule adapter updated");
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        return s;
    }

    private void setupTransition(){
        Transition enterTrans = new Slide();
        getWindow().setEnterTransition(enterTrans);

        Transition returnTrans = new Slide();
        getWindow().setReturnTransition(returnTrans);
    }
}