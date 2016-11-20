package com.workshop2.mykids.other;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;

/**
 * Created by MingHan on 28/10/2016.
 */

public class ParallaxController {
    private Activity context;
    private View viewBack, viewTop, viewFront;
    private float backSpeed, topSpeed, frontSpeed;
    private float maxMove;
    private SensorManager sensorManager;
    private ParallaxSensorEventListener eventListener;

    public ParallaxController(Activity context, View viewBack, View viewTop, View viewFront, float backSpeed, float topSpeed, float frontSpeed, float maxMove) {
        this.context = context;
        this.viewBack = viewBack;
        this.viewTop = viewTop;
        this.viewFront = viewFront;
        this.backSpeed = backSpeed;
        this.topSpeed = topSpeed;
        this.frontSpeed = frontSpeed;
        this.maxMove = maxMove;
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        eventListener = new ParallaxSensorEventListener();
    }

    public void startControl() {
        sensorManager.registerListener(eventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_GAME);
    }

    public void stopControl() {
        sensorManager.unregisterListener(eventListener);
    }

    private class ParallaxSensorEventListener implements SensorEventListener {

        float lastPitch = 0;

        public boolean isTablet(Context context) {
            return (context.getResources().getConfiguration().screenLayout
                    & Configuration.SCREENLAYOUT_SIZE_MASK)
                    >= Configuration.SCREENLAYOUT_SIZE_LARGE;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float pitch;
            if (isTablet(context)) {
                pitch = sensorEvent.values[2];
            } else {
                pitch = sensorEvent.values[1];
            }


            if (pitch > 45 || pitch < -45)
                return;

            float dp = pitch - lastPitch;
            if (dp == 0)
                return;
            float move = (dp * maxMove) / -45.0f;
            float maxSpeed = Math.max(backSpeed, Math.max(frontSpeed, topSpeed));

            Display display=context.getWindowManager().getDefaultDisplay();
            Point size=new Point();
            display.getSize(size);

            float backX = (move / maxSpeed) * backSpeed;
            float topX = (move / maxSpeed) * topSpeed;
            float frontX = (move / maxSpeed) * frontSpeed;

            if (viewBack != null) {
                float backSpace=(viewBack.getWidth()*(viewBack.getScaleX()-1))/2;
                if (viewBack.getX() + backX-backSpace > 0 || viewBack.getX() + backX +viewBack.getWidth()+backSpace < size.x)
                    return;
            } else backX=0;

            if (viewFront != null) {
                float frontSpace=(viewFront.getWidth()*(viewFront.getScaleX()-1))/2;
                if (viewFront.getX() + frontX - frontSpace> 0  || viewFront.getX() + frontX +viewFront.getWidth()+frontSpace < size.x)
                    return;
            } else frontX=0;


            if (viewTop != null) {
                float topSpace=(viewTop.getWidth()*(viewTop.getScaleX()-1))/2;
                if (viewTop.getX() + topX - topSpace > 0 || viewTop.getX() + topX +viewTop.getWidth()+topSpace < size.x)
                    return;
            } else topX=0;

            if (backX!=0) viewBack.setX(viewBack.getX() + backX);
            if (frontX!=0) viewFront.setX(viewFront.getX() + frontX);
            if (topX!=0) viewTop.setX(viewTop.getX() + topX);

            lastPitch = pitch;
            Log.d("TAG",viewBack.getX()+"");
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }
}