package com.workshop2.mykids.firebaseSync;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by MingHan on 17/11/2016.
 */

public class KidSync extends MultiDexApplication{
    @Override
    public void onCreate(){
        super.onCreate();
        FirebaseApp.initializeApp(this);
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
