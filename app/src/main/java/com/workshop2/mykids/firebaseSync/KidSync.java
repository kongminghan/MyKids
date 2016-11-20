package com.workshop2.mykids.firebaseSync;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MingHan on 17/11/2016.
 */

public class KidSync extends Application{
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
