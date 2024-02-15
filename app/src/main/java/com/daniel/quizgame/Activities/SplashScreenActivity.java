package com.daniel.quizgame.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.daniel.quizgame.R;

public class SplashScreenActivity extends AppCompatActivity {

    ConstraintLayout constraintLayout;

    private final int DURATION=5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.splash);
        constraintLayout.startAnimation(animation);

        new Handler(Looper.myLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this,LoginActivity.class);
                startActivity(i);
                finish();
            }
        },DURATION);


    }
}