package com.example.demo_fbfmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.adapter.FoodAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.ui.HomeActivity;
import com.example.demo_fbfmobile.ui.LoginActivity;
import com.example.demo_fbfmobile.ui.RegisterActivity;
import com.example.demo_fbfmobile.ui.ResetPasswordActivity;
import com.example.demo_fbfmobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private ApiService api;

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

        rvFoods = findViewById(R.id.rvFoods);
        adapter = new FoodAdapter();
        rvFoods.setAdapter(adapter);
        rvFoods.setLayoutManager(new GridLayoutManager(this, 2));

        // khi nhấn +
        adapter.setOnAddClickListener(food -> {
            TokenManager tokenManager = new TokenManager(MainActivity.this);
            if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
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
                        Toast.makeText(MainActivity.this,
                                "Fetch failed: " + res.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                    Toast.makeText(MainActivity.this,
                            "Network error", Toast.LENGTH_SHORT).show();
                }
            });
    }
}