package com.e.anApp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.SupportMapFragment;

public class SensorService extends Service implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor stepDetectorSensor;
    boolean isbtn_start = false;
    boolean isbtn_reset=false;
    boolean isbtn_end= false;
    private  int mStepDetector;
    TextView tvStepDetector, tvStepDistance, tvStepCal;
    private int count;
    private boolean isStop;
    public SensorService() {
    }

    public float toDis(int var) {
        float resultDis = 73*var/100;
        return resultDis;
    }

    public int toCal(int var) {
        int resultCal = var/30;
        return resultCal;
    }
    IMyCounterService.Stub binder = new IMyCounterService.Stub() {
        @Override
        public int getCount() throws RemoteException {
            return mStepDetector;
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);

        Thread counter = new Thread(new Counter());
        counter.start();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);


        if(stepDetectorSensor !=null) {
            sensorManager.registerListener(this, stepDetectorSensor, SensorManager.SENSOR_DELAY_GAME);

        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
        if(sensorManager !=null) {
            sensorManager.unregisterListener(this);
        }
    }





    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_STEP_DETECTOR) {
            //mStepDetector = (int) event.values[0];

            mStepDetector++;
            // Toast.makeText(SensorService.this, "걸음수:"+ mStepDetector, Toast.LENGTH_LONG).show();
            //mStepDetector =(int) event.values[0];




            //mStepDetector = (int) event.values[0];
            //toDis(mStepDetector);
            //toCal(mStepDetector);


                /*
                tvStepDetector.setText(String.valueOf(mStepDetector) + " 보");
            tvStepDistance.setText(String.valueOf(toDis(mStepDetector)) + " m");
            tvStepCal.setText(String.valueOf(toCal(mStepDetector)) + " kcal");

                 */
        }

    }
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        //return null;
        return binder;

    }
    @Override
    public boolean onUnbind(Intent intent) {
        isStop = true;
        return super.onUnbind(intent);
    }

    private class Counter implements Runnable {
        private Handler handler = new Handler();
        @Override public void run() {



            handler.post(new Runnable() {
                @Override public void run() {
                    // Toast로 Count 띄우기
                    //Toast.makeText(SensorService.this, "걸음수:"+ mStepDetector, Toast.LENGTH_LONG).show();
                }
            }); // Sleep을 통해 1초씩 쉬도록 한다.
            try {Thread.sleep(1000);
            } catch (InterruptedException e) {e.printStackTrace();
            }
        }

    }

}






