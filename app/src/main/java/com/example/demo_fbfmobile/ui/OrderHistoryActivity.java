package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.HistoryOrderPaidActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.OrderAdapter;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

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
        adapter     = new OrderAdapter();
        rvOrders.setAdapter(adapter);
//        tabLayout.addTab(tabLayout.newTab().setText("PAID"));
//        tabLayout.addTab(tabLayout.newTab().setText("PENDING"));
        String[] tabTitles = {"PAID", "PEND"};
        for (String title : tabTitles) {
            TabLayout.Tab tab = tabLayout.newTab();
            View customTabView = LayoutInflater.from(this).inflate(R.layout.item_tabintablayout, null);
            TextView textView = customTabView.findViewById(R.id.tab_text);
            textView.setText(title);
            tab.setCustomView(customTabView);
            tabLayout.addTab(tab);
        }
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        fetchOrderHistoryPaid(0, 20, "createdAt,desc");
                        break;
                    case 1:
                        fetchOrderHistoryPendding(0, 20, "createdAt,desc");
                        break;
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}

        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void fetchOrderHistoryPaid(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        if (res.isSuccessful() && res.body()!=null) {
                            List<FbfOrderDto> orders = new ArrayList<>();
                            for(int i =0; i < res.body().getContent().size(); i++){
                                if(res.body().getContent().get(i).getStatus().equals("PAID")){
                                    orders.add(res.body().getContent().get(i));
                                }
                            }
                            adapter.setOrders(orders);
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
    private void fetchOrderHistoryPendding(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        if (res.isSuccessful() && res.body()!=null) {
                            List<FbfOrderDto> orders = new ArrayList<>();
                            for(int i =0; i < res.body().getContent().size(); i++){
                                if(res.body().getContent().get(i).getStatus().equals("PENDING")){
                                    orders.add(res.body().getContent().get(i));
                                }
                            }
                            adapter.setOrders(orders);
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