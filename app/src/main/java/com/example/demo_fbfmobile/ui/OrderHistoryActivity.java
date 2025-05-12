package com.example.demo_fbfmobile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.OrderAdapter;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;
import com.google.android.material.tabs.TabLayout;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderHistoryActivity extends AppCompatActivity {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private String token;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        TokenManager tokenManager = new TokenManager(this);
        token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            finish();
        }

        rvOrders    = findViewById(R.id.rvOrders);
        tabLayout = findViewById(R.id.tabLayout);
        tabLayout.addTab(tabLayout.newTab().setText("Paid"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));
        adapter     = new OrderAdapter();
        rvOrders.setAdapter(adapter);
        fetchOrderHistory(0, 20, "createdAt,desc");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchOrderHistory(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        if (res.isSuccessful() && res.body()!=null) {
                            adapter.setOrders(res.body().getContent());
                            Log.d("OrderHistoryActivity", "Order history fetched successfully");
                        } else {
                            Toast.makeText(OrderHistoryActivity.this,
                                    "Lỗi: " + res.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<FbfOrderDto>> call, Throwable t) {
                        Toast.makeText(OrderHistoryActivity.this,
                                "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

}