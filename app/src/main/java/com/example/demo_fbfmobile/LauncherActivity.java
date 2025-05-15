package com.example.demo_fbfmobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class LauncherActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 1000; // 1.5 giây

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences("my_prefs", MODE_PRIVATE);
            boolean isFirstRun = prefs.getBoolean("is_first_run", true);

            Intent intent;
            if (isFirstRun) {
                intent = new Intent(this, OnboardingActivity.class);
                prefs.edit().putBoolean("is_first_run", false).apply();
            } else {
                intent = new Intent(this, MainActivity.class);
            }

            startActivity(intent);
            finish(); // không quay lại màn hình này nữa
        }, SPLASH_DELAY);
    }
}
