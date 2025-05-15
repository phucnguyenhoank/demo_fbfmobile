package com.example.demo_fbfmobile.ui;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.ResetPasswordRequest;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;

import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText etEmail, etOtp, etNewPassword;
    private Button btnSendOtp, btnResetPassword;
    private TextView tvTimer;
    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private final long OTP_VALID_DURATION = 5 * 60 * 1000; // 5 minutes in milliseconds
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components
        etEmail = findViewById(R.id.et_email);
        etOtp = findViewById(R.id.et_otp);
        etNewPassword = findViewById(R.id.et_new_password);
        btnSendOtp = findViewById(R.id.btn_send_otp);
        btnResetPassword = findViewById(R.id.btn_reset_password);
        tvTimer = findViewById(R.id.tv_timer);

        // Initialize Retrofit API service
        apiService = ApiClient.getApiService();

        // Set button click listeners
        btnSendOtp.setOnClickListener(v -> sendOtpRequest());
        btnResetPassword.setOnClickListener(v -> resetPasswordRequest());

        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void sendOtpRequest() {
        String email = etEmail.getText().toString().trim();
        // Validate email
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
            return;
        }

        Call<ApiResponse<String>> call = apiService.requestPasswordReset(email);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        startOtpTimer();
                        Toast.makeText(ResetPasswordActivity.this, "OTP sent successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to send OTP", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startOtpTimer() {
        if (isTimerRunning) {
            countDownTimer.cancel();
        }

        tvTimer.setVisibility(View.VISIBLE);
        btnSendOtp.setEnabled(false);

        countDownTimer = new CountDownTimer(OTP_VALID_DURATION, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60;
                tvTimer.setText(String.format("OTP valid for %d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvTimer.setText("OTP expired");
                btnSendOtp.setEnabled(true);
                isTimerRunning = false;
            }
        }.start();

        isTimerRunning = true;
    }

    private void resetPasswordRequest() {
        String email = etEmail.getText().toString().trim();
        String otp = etOtp.getText().toString().trim();
        String newPassword = etNewPassword.getText().toString().trim();

        // Validate inputs
        if (email.isEmpty() || otp.isEmpty() || newPassword.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setEmail(email);
        request.setOtp(otp);
        request.setNewPassword(newPassword);

        Call<ApiResponse<String>> call = apiService.resetPassword(request);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<String> apiResponse = response.body();
                    Toast.makeText(ResetPasswordActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    if (apiResponse.isSuccess()) {
                        finish(); // Close activity on success
                    }
                } else {
                    Toast.makeText(ResetPasswordActivity.this, "Failed to reset password", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}