package com.ighub.agrocity.Activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ighub.agrocity.R;

public class Splash extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity( new Intent(Splash.this, Login.class));
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
