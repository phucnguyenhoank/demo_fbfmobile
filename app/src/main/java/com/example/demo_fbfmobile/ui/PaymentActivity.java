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

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity {
    private TextView tvOrderId, textAddress, texttotalPrice;
    private TextView textTimer;
    private Button btnPay;
    private Long orderId;
    private Double totalPrice;
    private String address;
    private CountDownTimer countDownTimer;
    private boolean isPaid = false;

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
        textTimer = findViewById(R.id.textTimer);
        btnPay = findViewById(R.id.btnPay);
        texttotalPrice = findViewById(R.id.textTotalPrice);
        textAddress = findViewById(R.id.textAddress);
        orderId = getIntent().getLongExtra("orderId", -1);
        totalPrice = getIntent().getDoubleExtra("totalPrice", 0);
        address = getIntent().getStringExtra("address");
        if (totalPrice == 0 || address == null){
            getTotalPriceAndAddressByOrderId(orderId);
        }
        Log.d("Payment activity", "orderId: " + orderId + " totalPrice: " + totalPrice + " address: " + address);
        tvOrderId.setText("OrderID: " + orderId);
        textAddress.setText(address);
        texttotalPrice.setText(String.format("%.2f VND", totalPrice));
        if (orderId == -1) {
            Toast.makeText(this, "Không tìm thấy đơn hàng", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Khởi động bộ đếm ngược 3 phút
        startPaymentTimer();

        btnPay.setOnClickListener(v -> {
            isPaid = true;
            countDownTimer.cancel();
            btnPay.setEnabled(false);
            confirmOrder();
        });
    }

    private void startPaymentTimer() {
        countDownTimer = new CountDownTimer(3 * 60 * 1000, 1000) { // 3 phút
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
                    finish(); // Đóng Activity khi hết thời gian
                }
            }
        }.start();
    }
    private void getTotalPriceAndAddressByOrderId(Long orderid){
        String token = new TokenManager(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String authToken = "Bearer " + token;
        ApiService apiService = ApiClient.getApiService();
        apiService.getOrderByOrderId(authToken, orderid).enqueue(new Callback<ApiResponse<FbfOrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfOrderDto>> call, Response<ApiResponse<FbfOrderDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d("Payment activity", "Lấy Address, TotalPrice thành công" + response.body() + orderid);
                    texttotalPrice.setText(String.format("%.2f VND", response.body().getData().getDiscountedTotalPrice()));
                    textAddress.setText(response.body().getData().getAddress());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FbfOrderDto>> call, Throwable t) {
                Log.d("Payment activity", "Loi Kết nối");
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
                    // Chuyển sang MainActivity và xóa stack
                    Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
//                    finish(); // Đóng PaymentActivity sau khi chuyển
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Lỗi không xác định";
                    Toast.makeText(PaymentActivity.this, "Thanh toán thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                    btnPay.setEnabled(true); // Cho phép thử lại nếu thất bại
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                Toast.makeText(PaymentActivity.this, "Lỗi mạng: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                btnPay.setEnabled(true); // Cho phép thử lại nếu lỗi mạng
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel(); // Hủy bộ đếm khi activity bị hủy
        }
    }
}