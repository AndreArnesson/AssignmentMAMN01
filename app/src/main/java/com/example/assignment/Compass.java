package com.example.assignment;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Compass extends AppCompatActivity implements SensorEventListener {
    private TextView textView;

    private ImageView imageView;

    private SensorManager sensorManager;
    private Sensor acceleratorSensor, magnetometerSensor;

    private float[] lastAcceleratormeter = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] orientation = new float[3];

    boolean isLastAcceleratorArrayCopied = false;
    boolean isLastMagnetometerArrayCopied = false;

    long lastUpdatedTime = 0;
    float currentDegree = 0f;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        textView = findViewById(R.id.direction);

        imageView = findViewById(R.id.imageView);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        acceleratorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);


        Button home = (Button) findViewById(R.id.homeButton);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(homeIntent);
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor == acceleratorSensor){
            System.arraycopy(event.values,  0, lastAcceleratormeter,0, event.values.length);
            isLastAcceleratorArrayCopied = true;
        }else if(event.sensor == magnetometerSensor){
            System.arraycopy(event.values, 0, lastMagnetometer, 0 , event.values.length);
            isLastMagnetometerArrayCopied = true;
        }

        if(isLastMagnetometerArrayCopied && isLastAcceleratorArrayCopied && System.currentTimeMillis() - lastUpdatedTime > 250){
            SensorManager.getRotationMatrix(rotationMatrix, null, lastAcceleratormeter, lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix, orientation);

            float azimuthInRadian = orientation[0];
            float azimuthInDegree = (float) Math.toDegrees(azimuthInRadian);

            RotateAnimation rotateAnimation =
                    new RotateAnimation(currentDegree, -azimuthInDegree, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            imageView.startAnimation(rotateAnimation);

            currentDegree = -azimuthInDegree;
            lastUpdatedTime = System.currentTimeMillis();

            int degree = (int)azimuthInDegree;
            textView.setText(degree + "°");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, acceleratorSensor, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, magnetometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this, acceleratorSensor);
        sensorManager.unregisterListener(this, magnetometerSensor);
    }
}