package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.AuthenticationRequest;
import com.example.demo_fbfmobile.model.AuthenticationResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;
    private ApiService apiService;
    private TextView txtDangky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtDangky = findViewById(R.id.txtdangky);
        txtDangky.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
        apiService = ApiClient.getClient().create(ApiService.class);
        btnLogin.setOnClickListener(v -> authenticateUser());
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay lại Activity trước đó
                finish();
            }
        });
    }

    private void authenticateUser() {
        String username = etUsername.getText().toString();
        String password = etPassword.getText().toString();

        AuthenticationRequest request = new AuthenticationRequest(username, password);
        Call<AuthenticationResponse> call = apiService.authenticate(request);

        call.enqueue(new Callback<AuthenticationResponse>() {
            @Override
            public void onResponse(@NonNull Call<AuthenticationResponse> call, @NonNull Response<AuthenticationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    // 🔐 Save token and cartID to SharedPreferences
                    TokenManager tokenManager = new TokenManager(LoginActivity.this);
                    tokenManager.saveToken(token);
                    Log.d("LoginActivity", "Token saved: " + token);

                    // ✅ Now you can navigate to the next screen or home
                    Toast.makeText(LoginActivity.this, "Login successful!", Toast.LENGTH_SHORT).show();
                    // Navigate to HomeActivity
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoginActivity.this, "Login failed!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AuthenticationResponse> call, @NonNull Throwable t) {
                Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    // Phương thức xử lý click cho txtForgotPassword
    public void onForgotPasswordClick(View view) {
        Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
        startActivity(intent);
    }

    // Phương thức xử lý click cho txtdangky
    public void onRegisterClick(View view) {
        Toast.makeText(this, "Register clicked!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

}