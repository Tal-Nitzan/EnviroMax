package com.example.enviromax;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

@RequiresApi(api = Build.VERSION_CODES.O)
public class Activity_Splash extends AppCompatActivity {

    private final static int ANIMATION_DURATION = 2000;
    private ImageView splash_IMG_logo;
    private MaterialButton splash_BTN_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Utils.fullScreenCall(this);
        Activity_Splash.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        findViews();
        initViews();
        startAnimation(splash_IMG_logo);
    }

    private void findViews() {
        splash_IMG_logo = findViewById(R.id.splash_IMG_logo);
        splash_BTN_start = findViewById(R.id.splash_BTN_start);
    }

    private void initViews() {
        splash_BTN_start.setVisibility(View.INVISIBLE);

        splash_BTN_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                FirebaseUser user = firebaseAuth.getCurrentUser();
                Intent myIntent = new Intent(Activity_Splash.this, MainActivity.class);;
                startActivity(myIntent);
                finish();
            }
        });
    }

    private void startAnimation(View view) {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        view.setY(-(float)height / 2);

        view.animate()
                .translationY(0)
                .setDuration(ANIMATION_DURATION)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        splash_BTN_start.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) { }

                    @Override
                    public void onAnimationRepeat(Animator animator) { }
                });
    }
}