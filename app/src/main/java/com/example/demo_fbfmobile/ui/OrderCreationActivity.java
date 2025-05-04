package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.OrderItemAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDisplay;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.model.FbfUserDto;
import com.example.demo_fbfmobile.model.OrderRequest;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderCreationActivity extends AppCompatActivity {
    private List<CartItemDisplay> selectedItems;
    private EditText editPhone, editAddress, editDiscountCode;
    private Button btnConfirmOrder;
    private RecyclerView recyclerViewOrderItems;
    private TextView textTotalPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_creation);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        selectedItems = getIntent().getParcelableArrayListExtra("selectedItems");
        if (selectedItems == null || selectedItems.isEmpty()) {
            Toast.makeText(this, "Không có sản phẩm nào được chọn", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        recyclerViewOrderItems = findViewById(R.id.recyclerViewOrderItems);
        editPhone = findViewById(R.id.editPhone);
        editAddress = findViewById(R.id.editAddress);
        editDiscountCode = findViewById(R.id.editDiscountCode);
        btnConfirmOrder = findViewById(R.id.btnConfirmOrder);
        textTotalPrice = findViewById(R.id.textTotalPrice);

        // Hiển thị danh sách selectedItems với OrderItemAdapter
        OrderItemAdapter adapter = new OrderItemAdapter(selectedItems);
        recyclerViewOrderItems.setAdapter(adapter);
        recyclerViewOrderItems.setLayoutManager(new LinearLayoutManager(this));

        // Hiển thị tổng giá
        updateTotalPrice();

        // Lấy thông tin người dùng từ endpoint
        loadUserInfo();

        btnConfirmOrder.setOnClickListener(v -> {
            String phone = editPhone.getText().toString().trim();
            String address = editAddress.getText().toString().trim();
            String discountCode = editDiscountCode.getText().toString().trim();
            if (phone.isEmpty() || address.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập số điện thoại và địa chỉ", Toast.LENGTH_SHORT).show();
                return;
            }

            List<Long> selectedCartItemIds = new ArrayList<>();
            for (CartItemDisplay item : selectedItems) {
                selectedCartItemIds.add(item.getId());
            }
            createOrder(phone, address, selectedCartItemIds, discountCode);
        });
    }

    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItemDisplay item : selectedItems) {
            double discountedPrice = item.getPrice() * (1 - item.getDiscountPercentage() / 100);
            totalPrice += discountedPrice * item.getQuantity();
        }
        textTotalPrice.setText(String.format("Tổng giá: %.2f VND", totalPrice));
    }

    private void loadUserInfo() {
        String token = new TokenManager(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String authToken = "Bearer " + token;

        ApiService apiService = ApiClient.getApiService();
        Call<ApiResponse<FbfUserDto>> call = apiService.getCurrentUser(authToken);
        call.enqueue(new Callback<ApiResponse<FbfUserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfUserDto>> call, Response<ApiResponse<FbfUserDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    FbfUserDto user = response.body().getData();
                    editPhone.setText(user.getPhoneNumber());
                    editAddress.setText(user.getAddress());
                } else {
                    Toast.makeText(OrderCreationActivity.this, "Không thể tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FbfUserDto>> call, Throwable t) {
                Toast.makeText(OrderCreationActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createOrder(String phone, String address, List<Long> selectedCartItemIds, String discountCode) {
        String token = new TokenManager(this).getToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "Vui lòng đăng nhập lại", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            return;
        }
        String authToken = "Bearer " + token;

        ApiService apiService = ApiClient.getApiService();
        OrderRequest request = new OrderRequest(phone, address, selectedCartItemIds, discountCode);
        Call<ApiResponse<FbfOrderDto>> call = apiService.createOrder(authToken, request);
        call.enqueue(new Callback<ApiResponse<FbfOrderDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfOrderDto>> call, Response<ApiResponse<FbfOrderDto>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    FbfOrderDto orderDto = response.body().getData();
                    Intent intent = new Intent(OrderCreationActivity.this, PaymentActivity.class);
                    intent.putExtra("orderId", orderDto.getId());
                    startActivity(intent);
                    setResult(RESULT_OK);
                    finish();
                } else {
                    String errorMsg = response.body() != null ? response.body().getMessage() : "Lỗi không xác định";
                    Toast.makeText(OrderCreationActivity.this, "Tạo đơn hàng thất bại: " + errorMsg, Toast.LENGTH_SHORT).show();
                    setResult(RESULT_CANCELED);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<FbfOrderDto>> call, Throwable t) {
                Toast.makeText(OrderCreationActivity.this, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED);
            }
        });
    }
}