package io.cluo29.github.sensordemo;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service  implements SensorEventListener {
    private static float accelerometer_x = 0;
    private static float accelerometer_y = 0;
    private static float accelerometer_z = 0;


    SensorManager mSensorMgr;

    Handler handler;

    HandlerThread mHandlerThread;

    int usedTime = 0;

    public MyService() {
    }

    @Override
    public void onDestroy() {

        Log.d("haha", " service stopped...");
    }

    @Override
    public void onCreate() {
        //here is still main thread!

        //dont do anything heavy here



        mSensorMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        mHandlerThread = new HandlerThread("sensorThread");

        mHandlerThread.start();

        //to stop
        //mHandlerThread.quitSafely();

        handler = new Handler(mHandlerThread.getLooper());

        mSensorMgr.registerListener(this, mSensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST, handler);
        //handle it in a new thread

        //if service paused, even if you kill activity, thread will run unless you close it

        //close sensor

        //mSensorMgr.unregisterListener(this);


        //write a new thread for compuation

        ComputationRun computation=new ComputationRun();
        Thread computationThread=new Thread(computation);
        computationThread.start();


        //to close compuation thread

        //computationThread.interrupt();

    }

    class ComputationRun implements  Runnable
    {
        // volatile makes sure reading it from memory, not cache,
        volatile boolean isRunning = false;
        @Override
        public void run() {

            isRunning = true;
            //

            while (isRunning)
            {

                usedTime = usedTime + 1;

                //Log.d("SENSORS", "accelerometer_x= " + accelerometer_x);


                Log.d("SENSORS", "accelerometer_x= " + accelerometer_x);
                Log.d("SENSORS", "accelerometer_y= " + accelerometer_y);
                Log.d("SENSORS", "accelerometer_z= " + accelerometer_z);



                if (usedTime>12)
                    Thread.currentThread().interrupt();



                if(Thread.currentThread().isInterrupted())  //Thread refers to current thread
                {
                    // release resources

                    mSensorMgr.unregisterListener(MyService.this);


                    mHandlerThread.quitSafely();

                    isRunning = false;

                    // quit the service if u want

                    //MyService.this.stopSelf();

                    return;
                }



                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                    e.printStackTrace();

                    // release resources
                    mSensorMgr.unregisterListener(MyService.this);

                    mHandlerThread.quitSafely();

                    isRunning = false;

                    // quit the service if u want

                    //MyService.this.stopSelf();
                }
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // accelerometer data
            accelerometer_x = event.values[0];
            accelerometer_y = event.values[1];
            accelerometer_z = event.values[2];



            //Log.d("SENSORS", "accelerometer_x= " + accelerometer_x);
            //Log.d("SENSORS", "accelerometer_y= " + accelerometer_y);
            //Log.d("SENSORS", "accelerometer_z= " + accelerometer_z);

        }
    }
}

