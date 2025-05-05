package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.FoodAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ApiService apiService;
    private TextView tvResult;
    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private ApiService api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvResult = findViewById(R.id.tvResult);
        apiService = ApiClient.getClient().create(ApiService.class);
        callSecuredEndpoint();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            // Clear token
            TokenManager tokenManager = new TokenManager(HomeActivity.this);
            tokenManager.clearToken();

            // Go back to LoginActivity
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
            startActivity(intent);
            finish(); // Close HomeActivity
        });

        Button btnCart = findViewById(R.id.btnCart);
        btnCart.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CartActivity.class);
            startActivity(intent);
        });

        Button btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        Button btnOrderHistory = findViewById(R.id.btnOrderHistory);
        btnOrderHistory.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
            startActivity(intent);
        });

        rvFoods = findViewById(R.id.rvFoods);
        adapter = new FoodAdapter();
        rvFoods.setAdapter(adapter);
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));

        // khi nhấn +
        adapter.setOnAddClickListener(food -> {
            TokenManager tokenManager = new TokenManager(this);
            String token = tokenManager.getToken();
            if (token == null) {
                Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }
            // Giả sử chọn size đầu tiên và quantity = 1
            Long sizeId = food.getSizes().get(0).getId();
            CartItemRequest req = new CartItemRequest(sizeId, 1);
            api.addCartItem("Bearer " + token, req)
                    .enqueue(new Callback<ApiResponse<CartItemDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<CartItemDto>> call,
                                               Response<ApiResponse<CartItemDto>> response) {
                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(HomeActivity.this,
                                        "Added to cart: " + response.body().getData().getId(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(HomeActivity.this,
                                        "Add failed: " + response.code(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                            Toast.makeText(HomeActivity.this,
                                    "Network error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        api = ApiClient.getClient().create(ApiService.class);
        fetchFoods(0, 10, "name,asc");
    }

    private void fetchFoods(int page, int size, String sort) {
        api.getAllFoods(page, size, sort)
            .enqueue(new Callback<PageResponse<FoodDto>>() {
                @Override public void onResponse(Call<PageResponse<FoodDto>> call,
                                                 Response<PageResponse<FoodDto>> res) {
                    if (res.isSuccessful() && res.body()!=null) {
                        adapter.setData(res.body().getContent());
                    } else {
                        Toast.makeText(HomeActivity.this,
                                "Fetch failed: " + res.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                    Toast.makeText(HomeActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void callSecuredEndpoint() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token != null) {
            Call<ApiResponse<String>> call = apiService.getSecuredData("Bearer " + token);
            call.enqueue(new Callback<ApiResponse<String>>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String result = response.body().getData();
                        String username = tokenManager.getUsername();
                        boolean expired = tokenManager.isTokenExpired();
                        String displayText = "Secured data: " + result + "\nUser: " + username + "\nExpired: " + expired;
                        tvResult.setText(displayText);
                    } else {
                        tvResult.setText("❌ Failed to access secured API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                    tvResult.setText("⚠️ Error: " + t.getMessage());
                }
            });
        }
        else {
            tvResult.setText("No token found. Please login.");
        }
    }
}