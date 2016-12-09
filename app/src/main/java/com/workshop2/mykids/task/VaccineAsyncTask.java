package com.workshop2.mykids.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.VaccineAdapter;
import com.workshop2.mykids.model.Vaccine;

import java.util.ArrayList;

/**
 * Created by MingHan on 17/11/2016.
 */

public class VaccineAsyncTask extends AsyncTask<Void, Void, ArrayList<Vaccine>>{

    private Context context;
    private VaccineAdapter vaccineAdapter;
    private RecyclerView recyclerView;

    public VaccineAsyncTask(Context context, VaccineAdapter vaccineAdapter, RecyclerView recyclerView){
        this.context = context;
        this.vaccineAdapter = vaccineAdapter;
        this.recyclerView = recyclerView;
    }
    @Override
    protected ArrayList<Vaccine> doInBackground(Void... params) {
        final ArrayList<Vaccine> vaccines = new ArrayList<>();
        DatabaseReference firebase = FirebaseDatabase.getInstance().getReference("vaccines");
        firebase.keepSynced(true);
        firebase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vaccines.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Vaccine vaccine = postSnapshot.getValue(Vaccine.class);
                    vaccines.add(vaccine);
                }
                vaccineAdapter.notifyDataSetChanged();
//                System.out.println("NOTIFY DATA SET CHANGED");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return vaccines;
    }


    protected void onProgressUpdate(Void... params) {

    }

    protected void onPostExecute(ArrayList<Vaccine> result) {
        vaccineAdapter = new VaccineAdapter(context, result);
        vaccineAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(vaccineAdapter);
    }
}
