package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.CartItemAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDisplay;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private final String TAG = "CartActivity";

    private ApiService apiService;
    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItemDisplay> cartItems = new ArrayList<>();

    private Button btnCreateOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        apiService = ApiClient.getClient().create(ApiService.class);

        recyclerView = findViewById(R.id.recyclerViewCart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CartItemAdapter(cartItems, this);
        recyclerView.setAdapter(adapter);
        btnCreateOrder = findViewById(R.id.btnCreateOrder);

        fetchCartItems();

        btnCreateOrder.setOnClickListener(v -> {
            List<CartItemDisplay> selectedItems = new ArrayList<>();
            for (CartItemDisplay item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(this, "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(CartActivity.this, OrderCreationActivity.class);
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(selectedItems));
            startActivity(intent);
        });
    }

    private void fetchCartItems() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token != null) {
            Call<ApiResponse<List<CartItemDisplay>>> call = apiService.getCartItemsDisplay("Bearer " + token);
            call.enqueue(new Callback<ApiResponse<List<CartItemDisplay>>>() {
                @Override
                public void onResponse(Call<ApiResponse<List<CartItemDisplay>>> call, Response<ApiResponse<List<CartItemDisplay>>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<CartItemDisplay> data = response.body().getData();
                        cartItems.clear();
                        cartItems.addAll(data);
                        adapter.notifyDataSetChanged();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<List<CartItemDisplay>>> call, Throwable t) {
                    Log.e(TAG, "Error: " + t.getMessage());
                }
            });
        }
        else {
            Toast.makeText(CartActivity.this, "No token found. Please login.", Toast.LENGTH_SHORT).show();
        }

    }

}