package com.workshop2.mykids.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.HospitalAdapter;
import com.workshop2.mykids.model.Hospital;

import java.util.ArrayList;

/**
 * Created by MingHan on 18/11/2016.
 */

public class HospitalAsyncTask extends AsyncTask<Void, Void, ArrayList<Hospital>> {

    HospitalAdapter adapter;
    RecyclerView recyclerView;
    Context context;
    int width;

    public HospitalAsyncTask(HospitalAdapter adapter, RecyclerView recyclerView, Context context, int width){
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.context = context;
        this.width = width;
    }

    @Override
    protected ArrayList<Hospital> doInBackground(Void... params) {
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

    @Override
    protected void onPostExecute(ArrayList<Hospital> hospitals) {
//        super.onPostExecute(hospitals);
        adapter = new HospitalAdapter(context, hospitals, width);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }
}
