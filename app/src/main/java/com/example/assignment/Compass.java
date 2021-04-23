package com.example.assignment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Compass extends AppCompatActivity implements SensorEventListener {
    private TextView azimuth;
    private TextView direction;
    LinearLayout layout;

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

        azimuth = findViewById(R.id.azimuth);
        direction = findViewById(R.id.direction);
        imageView = findViewById(R.id.imageView);
        layout = findViewById(R.id.layout);

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

            int degree = (int)(azimuthInDegree+360)%360;
            azimuth.setText(degree + "°");

            direction.setText(getAzimuth(degree));



            if(degree<15 || degree>345) {
                layout.setBackgroundColor(Color.LTGRAY);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    v.vibrate(500);
                }
            } else {
                layout.setBackgroundColor(Color.WHITE);

            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getAzimuth(int angle){
        String direction = "";

        if (angle >= 350 || angle <= 10)
            direction = "N";
        if (angle < 350 && angle > 280)
            direction = "NW";
        if (angle <= 280 && angle > 260)
            direction = "W";
        if (angle <= 260 && angle > 190)
            direction = "SW";
        if (angle <= 190 && angle > 170)
            direction = "S";
        if (angle <= 170 && angle > 100)
            direction = "SE";
        if (angle <= 100 && angle > 80)
            direction = "E";
        if (angle <= 80 && angle > 10)
            direction = "NE";

        return direction;
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