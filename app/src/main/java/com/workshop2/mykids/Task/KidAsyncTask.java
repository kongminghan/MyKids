package com.workshop2.mykids.task;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.workshop2.mykids.adapter.KidAdapter;
import com.workshop2.mykids.model.Kid;

import java.util.ArrayList;

/**
 * Created by MingHan on 17/11/2016.
 */

public class KidAsyncTask extends AsyncTask<Void, Void, ArrayList<Kid>>{

    private Context context;
    private KidAdapter adapter;
    private RecyclerView recyclerView;
    public KidAsyncTask(Context context, KidAdapter adapter, RecyclerView recyclerView){
        this.context = context;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
    }

    @Override
    protected ArrayList<Kid> doInBackground(Void...params) {
        final ArrayList<Kid> kidList;
        DatabaseReference database = FirebaseDatabase.getInstance().getReference();

//        Firebase mRef = new Firebase("https://fir-mykids.firebaseio.com/");
        kidList = new ArrayList<Kid>();
        DatabaseReference userRef = database.child("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("kid");
        userRef.keepSynced(true);

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                kidList.clear();
                for(DataSnapshot postSnapshot : dataSnapshot.getChildren()){
                    Kid kid = postSnapshot.getValue(Kid.class);
                    kidList.add(kid);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getMessage());
            }
        });
        return kidList;
    }

    protected void onProgressUpdate(Void... params) {

    }

    protected void onPostExecute(ArrayList<Kid> result) {
        adapter = new KidAdapter(context, result);
        recyclerView.setAdapter(adapter);
    }
}
