package com.example.demo_fbfmobile;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.adapter.CartItemAdapter;
import com.example.demo_fbfmobile.adapter.CartItemOrderHistoryAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDisplay;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.OrderItemDetailDto;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryOrderPaidActivity extends AppCompatActivity {

    private ApiService apiService;
    private RecyclerView recyclerView;
    private CartItemOrderHistoryAdapter adapter;
    private List<OrderItemDetailDto> cartItems = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_order_paid);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerView = findViewById(R.id.rvHistoryOrderCartItem);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemOrderHistoryAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);
        fetchCartItemOrderHistory();
        ImageView backButton = findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    private void fetchCartItemOrderHistory(){
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        Long id = Long.valueOf(getIntent().getLongExtra("orderId", -1));
        String authoToken = "Bearer " + token;
        Log.d("History Order Paid", String.valueOf(id));
        if (token != null) {
            Call<ApiResponse<List<OrderItemDetailDto>>> call = apiService.getOrderItemByOrderId(authoToken, id);
            call.enqueue(new Callback<ApiResponse<List<OrderItemDetailDto>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<OrderItemDetailDto>>> call, Response<ApiResponse<List<OrderItemDetailDto>>> response) {
                    Log.d("History Order Paid", String.valueOf(response.code()));
                    if (response.isSuccessful() && response.body() != null){
                        List<OrderItemDetailDto> orderItemDetailDtos = response.body().getData();
                        cartItems.clear();
                        cartItems.addAll(orderItemDetailDtos);
                        adapter.notifyDataSetChanged();
                        Log.d("History Order Paid", "Success");
                    }
                    else {
                        Log.d("History Order Paid", "Failed");
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<OrderItemDetailDto>>> call, Throwable t) {
                    Log.d("History Order Paid", "Fail " +  authoToken + t.getMessage());
                }
            });
        }
    }
}