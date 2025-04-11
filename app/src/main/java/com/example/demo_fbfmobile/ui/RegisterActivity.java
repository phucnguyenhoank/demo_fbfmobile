package com.example.demo_fbfmobile.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etUsername, etPassword, etEmail, etName, etPhoneNumber, etAddress;
    private Button btnOk;

    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etName = findViewById(R.id.etName);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        etAddress = findViewById(R.id.etAddress);
        btnOk = findViewById(R.id.btnOk);

        apiService = ApiClient.getApiService();

        btnOk.setOnClickListener(v -> {
            final String username = etUsername.getText().toString().trim();
            final String password = etPassword.getText().toString().trim();
            final String email = etEmail.getText().toString().trim();
            final String name = etName.getText().toString().trim();
            final String phoneNumber = etPhoneNumber.getText().toString().trim();
            final String address = etAddress.getText().toString().trim();

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Đang gửi OTP...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            // Gọi API send OTP với email đã nhập
            Call<ApiResponse<String>> call = apiService.sendOtp(email);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {

                    progressDialog.dismiss();

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        // Nếu gửi OTP thành công, chuyển sang OtpActivity
                        Intent intent = new Intent(RegisterActivity.this, OtpActivity.class);
                        intent.putExtra("username", username);
                        intent.putExtra("password", password);
                        intent.putExtra("email", email);
                        intent.putExtra("name", name);
                        intent.putExtra("phoneNumber", phoneNumber);
                        intent.putExtra("address", address);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Gửi OTP thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(RegisterActivity.this, "Có lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}