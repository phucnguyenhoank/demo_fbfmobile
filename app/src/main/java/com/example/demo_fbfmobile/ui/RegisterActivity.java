package com.example.demo_fbfmobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etName, etPhoneNumber, etAddress;
    private Button btnRegister;

    private ApiService apiService;
    private NestedScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        scrollView = findViewById(R.id.scrollView);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnRegister = findViewById(R.id.btnRegister);
        etPhoneNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.scrollTo(0, v.getBottom()));
            }
        });
        etAddress.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                scrollView.post(() -> scrollView.scrollTo(0, v.getBottom()));
            }
        });

        apiService = ApiClient.getApiService();

        btnRegister.setOnClickListener(v -> {
            final String username = etUsername.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String name = etName.getText().toString().trim();
            final String phoneNumber = etPhoneNumber.getText().toString().trim();
            final String address = etAddress.getText().toString().trim();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Sending OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Gọi API send OTP với email đã nhập
            Call<ApiResponse<String>> call = apiService.sendOtp(email);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {

                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("email", email);
                        intent.putExtra("name", name);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Send OTP fail", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Error calling API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}