package com.example.assignment;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Accelerometer extends AppCompatActivity implements SensorEventListener{

    private TextView xText, yText, zText;
    private Sensor mySensor;
    private SensorManager sensorM;

    private MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        // Create sensormanager
        sensorM = (SensorManager) getSystemService(SENSOR_SERVICE);

        //accelerometer sensor
        mySensor = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Register sensor listener
        sensorM.registerListener(this, mySensor, SensorManager.SENSOR_DELAY_NORMAL);

        //Assign TextVIew
        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

        Button home = (Button) findViewById(R.id.homeButton2);
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
        double x = Math.round(event.values[0]*100.0)/100.0;
        double y = Math.round(event.values[1]*100.0)/100.0;
        double z = Math.round(event.values[2]*100.0)/100.0;
        xText.setText("X: " +  x);
        yText.setText("Y: " + y);
        zText.setText("Z: " + z);

        if(z<-8 && z>-10) {
            if (player == null) {
                player = MediaPlayer.create(this, R.raw.upsidedown);
            }
            player.start();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        //Not in use
    }
}