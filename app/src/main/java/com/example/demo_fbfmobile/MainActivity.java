package com.example.demo_fbfmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.ui.HomeActivity;
import com.example.demo_fbfmobile.ui.LoginActivity;
import com.example.demo_fbfmobile.ui.RegisterActivity;
import com.example.demo_fbfmobile.ui.ResetPasswordActivity;
import com.example.demo_fbfmobile.utils.TokenManager;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button eatButton = findViewById(R.id.btnEat);
        eatButton.setOnClickListener(v -> {
            TokenManager tokenManager = new TokenManager(MainActivity.this);
            if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                // Token valid, go to HomeActivity
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                // Token missing or expired, go to LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        Button registerButton = findViewById(R.id.btnRegister);
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        Button btnResetPassword = findViewById(R.id.btnResetPassword);
        btnResetPassword.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ResetPasswordActivity.class);
            startActivity(intent);
        });

    }
}