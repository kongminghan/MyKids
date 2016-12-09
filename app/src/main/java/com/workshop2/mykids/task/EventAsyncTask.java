package com.workshop2.mykids.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.EventAdapter;
import com.workshop2.mykids.model.Event;
import com.workshop2.mykids.model.Kid;
import com.workshop2.mykids.model.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by MingHan on 20/11/2016.
 */

public class EventAsyncTask extends AsyncTask<Void, Void, ArrayList<Event>>{

    private Context context;
    private EventAdapter eventAdapter;
    private RecyclerView recyclerView;
    private LinearLayout layoutEmpty;

    public EventAsyncTask(Context context, EventAdapter eventAdapter, RecyclerView recyclerView, LinearLayout layoutEmpty){
        this.context = context;
        this.eventAdapter = eventAdapter;
        this.recyclerView = recyclerView;
        this.layoutEmpty = layoutEmpty;
    }

    @Override
    protected ArrayList<Event> doInBackground(Void... params) {
        final SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        final Calendar c = Calendar.getInstance();
        final Date[] date = {null};
        final int[] i = {0};

        final Event event = new Event();
        final ArrayList<Schedule> schedules = new ArrayList<Schedule>();
        final ArrayList<Event> kids = new ArrayList<>();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("User")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("kid");

        databaseReference.keepSynced(true);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                kids.clear();
                if(dataSnapshot.hasChildren()){
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        Kid kid = postSnapshot.getValue(Kid.class);
                        i[0]=0; kids.add(new Event(kid.getKid_name(), kid.getKid_date(), 11));
                        if(kid.getSchedule()!=null){
                            for(Schedule s : kid.getSchedule()){
                                try {
                                    date[0] = formatter.parse(s.getS_date());
                                    c.setTime(date[0]);

                                    if(!c.before(Calendar.getInstance()) && i[0] <5){
//                                schedules.add(s);
                                        kids.add(new Event(s.getS_name(), s.getS_date(), s.getS_id(), s.getS_time(), kid.getKid_name(), kid.getKid_date(), kid.getKid_id(), 10));
                                        i[0]++;
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        else{
                            return;
                        }
                    }
                    eventAdapter.notifyDataSetChanged();

                    if(kids.isEmpty()){
                        recyclerView.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }else{
                        recyclerView.setVisibility(View.VISIBLE);
                        layoutEmpty.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
        return kids;
    }


    protected void onProgressUpdate(Void... params) {

    }

    protected void onPostExecute(ArrayList<Event> result) {
        eventAdapter = new EventAdapter(context, result, recyclerView);
        recyclerView.setAdapter(eventAdapter);
    }
}
