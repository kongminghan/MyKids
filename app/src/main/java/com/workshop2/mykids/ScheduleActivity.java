package com.workshop2.mykids;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.vision.text.Text;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.workshop2.mykids.Adapter.KidAdapter;
import com.workshop2.mykids.Adapter.ScheduleAdapter;
import com.workshop2.mykids.Model.Kid;
import com.workshop2.mykids.Model.Schedule;
import com.workshop2.mykids.Other.CircleTransform;
import com.workshop2.mykids.databinding.ActivityScheduleBinding;

import java.util.ArrayList;

import static com.bumptech.glide.load.engine.DiskCacheStrategy.ALL;

public class ScheduleActivity extends AppCompatActivity {
    private ArrayList<Schedule> scheduleList;
    private Kid kid;
    private ScheduleAdapter scheduleAdapter;
    ActivityScheduleBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_schedule);

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle("");
        binding.collapsingToolbar.setTitleEnabled(false);

        Intent intent = getIntent();
        scheduleList = (ArrayList<Schedule>) intent.getSerializableExtra("schedule");

        binding.tvName.setText(intent.getStringExtra("kname"));
        binding.tvDate.setText(intent.getStringExtra("kdate"));

        Glide.with(this)
                .load(intent.getStringExtra("kimage"))
                .bitmapTransform(new CircleTransform(this))
                .diskCacheStrategy(ALL)
                .into(binding.profileImage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){

            scheduleAdapter = new ScheduleAdapter(this, scheduleList);
            scheduleAdapter.notifyDataSetChanged();
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
            binding.rvSchedule.setLayoutManager(mLayoutManager);
            binding.rvSchedule.setItemAnimator(new DefaultItemAnimator());
            binding.rvSchedule.setAdapter(scheduleAdapter);
        }
    }

    private void loadSchedule(){}

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
}
