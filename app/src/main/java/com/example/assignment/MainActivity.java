package com.example.assignment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button accelerometer = (Button) findViewById(R.id.accelerometer);
        accelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent accIntent = new Intent(getApplicationContext(), Accelerometer.class);
                startActivity(accIntent);

            }
        });

        Button compass = (Button) findViewById(R.id.compass);
        compass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent compassIntent = new Intent(getApplicationContext(), Compass.class);
                startActivity(compassIntent);
            }
        });
    }
}