package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private TextView tvOrderId, textAddress, textTotalPrice, textTimer;
    private Button btnPay;
    private Long orderId;
    private Double totalPrice;
    private String address;
    private CountDownTimer countDownTimer;
    private boolean isPaid = false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payment);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvOrderId = findViewById(R.id.tvOrderId);
        textAddress = findViewById(R.id.textAddress);
        textTotalPrice = findViewById(R.id.textTotalPrice);
        textTimer = findViewById(R.id.textTimer);
        btnPay = findViewById(R.id.btnPay);

        orderId = getIntent().getLongExtra("orderId", -1);
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
        address = getIntent().getStringExtra("address");

        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvOrderId.setText("#: " + orderId);

        fetchOrderDetailsAndStartTimer();

        btnPay.setOnClickListener(v -> {
            isPaid = true;
            countDownTimer.cancel();
            btnPay.setEnabled(false);
            confirmOrder();
        });
    }

    private void fetchOrderDetailsAndStartTimer() {
        String token = new TokenManager(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String authToken = "Bearer " + token;
        ApiService apiService = ApiClient.getApiService();
        apiService.getOrderByOrderId(authToken, orderId).enqueue(new Callback<ApiResponse<FbfOrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfOrderDto>> call, Response<ApiResponse<FbfOrderDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    FbfOrderDto order = response.body().getData();

                    if (totalPrice == 0) {
                        totalPrice = order.getDiscountedTotalPrice();
                    }
                    if (address == null) {
                        address = order.getAddress();
                    }

                    textTotalPrice.setText(String.format("%.2f VND", totalPrice));
                    textAddress.setText(address);


                    String createdAtStr = order.getCreatedAt(); // ISO 8601, dạng chuỗi "2025-05-14T09:31:19.400409"
                    try {
                        Date createdAt = sdf.parse(createdAtStr);
                        long elapsedMillis = System.currentTimeMillis() - createdAt.getTime();
                        long remainingMillis = (3 * 60 * 1000) - elapsedMillis;
//
//                        LocalDateTime createdAt = LocalDateTime.parse(createdAtStr);
//                        Duration duration = Duration.between(createdAt, LocalDateTime.now());
//                        long elapsedMillis = duration.toMillis();
//                        long remainingMillis = (3 * 60 * 1000) - elapsedMillis;

                        if (remainingMillis > 0) {
                            countDownTimer = new CountDownTimer(remainingMillis, 1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    long seconds = millisUntilFinished / 1000;
                                    long minutes = seconds / 60;
                                    seconds = seconds % 60;
                                    textTimer.setText(String.format("Thời gian còn lại: %d:%02d", minutes, seconds));
                                }

                                @Override
                                public void onFinish() {
                                    if (!isPaid) {
                                        Toast.makeText(PaymentActivity.this, "Đơn hàng đã bị hủy do hết thời gian thanh toán", Toast.LENGTH_SHORT).show();
                                        redirectToMainActivity();
                                    }
                                }
                            }.start();
                        } else {
                            Toast.makeText(PaymentActivity.this, "Đơn hàng đã hết thời gian thanh toán", Toast.LENGTH_SHORT).show();
                            redirectToMainActivity();
                        }
                    } catch (Exception e) {
                        Log.e("PaymentActivity", "Lỗi khi phân tích createdAt: " + e.getMessage());
                        Toast.makeText(PaymentActivity.this, "Lỗi thời gian tạo đơn hàng", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(PaymentActivity.this, "Không thể lấy thông tin đơn hàng", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FbfOrderDto>> call, Throwable t) {
                Log.e("PaymentActivity", "Lỗi kết nối API: " + t.getMessage());
                Toast.makeText(PaymentActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void confirmOrder() {
        String token = new TokenManager(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String authToken = "Bearer " + token;

        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse<String>> call = apiService.confirmOrder(authToken, orderId);
        call.enqueue(new Callback<ApiResponse<String>>() {
            @Override
            public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(PaymentActivity.this, "Thanh toán thành công", Toast.LENGTH_LONG).show();
                    redirectToMainActivity();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Lỗi không xác định";
                    Toast.makeText(PaymentActivity.this, "Thanh toán thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                    btnPay.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnPay.setEnabled(true);
            }
        });
    }

    private void redirectToMainActivity() {
        Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}