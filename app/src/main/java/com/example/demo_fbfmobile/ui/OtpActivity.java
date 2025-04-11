package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.AuthenticationResponse;
import com.example.demo_fbfmobile.model.RegisterRequest;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OtpActivity extends AppCompatActivity {

    private TextView tvCountdown;
    private EditText etOtp;
    private Button btnSubmitOtp;
    private CountDownTimer countDownTimer;
    private static final long COUNTDOWN_TIME = 5 * 60 * 1000; // 5 phút

    // Dữ liệu đăng ký được chuyển từ RegisterActivity
    private String username, password, email, name, phoneNumber, address;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_otp);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvCountdown = findViewById(R.id.tvCountdown);
        etOtp = findViewById(R.id.etOtp);
        btnSubmitOtp = findViewById(R.id.btnSubmitOtp);

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        password = intent.getStringExtra("password");
        email = intent.getStringExtra("email");
        name = intent.getStringExtra("name");
        phoneNumber = intent.getStringExtra("phoneNumber");
        address = intent.getStringExtra("address");

        apiService = ApiClient.getApiService();

        startCountdown();

        btnSubmitOtp.setOnClickListener(v -> {
            String otp = etOtp.getText().toString().trim();

            // Tạo đối tượng RegisterRequest với đầy đủ dữ liệu và OTP
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setPassword(password);
            registerRequest.setEmail(email);
            registerRequest.setName(name);
            registerRequest.setPhoneNumber(phoneNumber);
            registerRequest.setAddress(address);
            registerRequest.setOtp(otp);

            // Gọi API register
            Call<AuthenticationResponse> call = apiService.register(registerRequest);
            call.enqueue(new Callback<AuthenticationResponse>() {
                @Override
                public void onResponse(Call<AuthenticationResponse> call, Response<AuthenticationResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Thành công, thực hiện hành động đăng nhập tự động (lưu token, chuyển trang,...)
                        String token = response.body().getToken();
                        Toast.makeText(OtpActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                        // Ví dụ chuyển sang MainActivity
                        Intent intent = new Intent(OtpActivity.this, MainActivity.class);
                        // Lưu token nếu cần qua SharedPreferences hoặc SessionManager
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(OtpActivity.this, "Xác thực OTP thất bại.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<AuthenticationResponse> call, Throwable t) {
                    Toast.makeText(OtpActivity.this, "Có lỗi khi gọi API: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
    private void startCountdown() {
        countDownTimer = new CountDownTimer(COUNTDOWN_TIME, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                int minutes = (int) (millisUntilFinished / 1000) / 60;
                int seconds = (int) (millisUntilFinished / 1000) % 60;
                tvCountdown.setText(String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                tvCountdown.setText("00:00");
                Toast.makeText(OtpActivity.this, "OTP đã hết hạn. Vui lòng gửi lại OTP!", Toast.LENGTH_LONG).show();
            }
        }.start();
    }

    @Override
    protected void onDestroy() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDestroy();
    }
}