package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

public class CartFragment extends Fragment {

    private final String TAG = "CartFragment";

    private ApiService apiService;
    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private List<CartItemDisplay> cartItems = new ArrayList<>();
    private Button btnCreateOrder;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo views
        recyclerView = view.findViewById(R.id.recyclerViewCart);
        btnCreateOrder = view.findViewById(R.id.btnCreateOrder);

        // Thiết lập RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new CartItemAdapter(cartItems, requireContext());
        recyclerView.setAdapter(adapter);

        // Khởi tạo API
        apiService = ApiClient.getClient().create(ApiService.class);

        // Lấy dữ liệu giỏ hàng
        fetchCartItems();

        // Thiết lập sự kiện click cho nút "Tạo đơn hàng"
        btnCreateOrder.setOnClickListener(v -> {
            List<CartItemDisplay> selectedItems = new ArrayList<>();
            for (CartItemDisplay item : cartItems) {
                if (item.isSelected()) {
                    selectedItems.add(item);
                }
            }
            if (selectedItems.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng chọn ít nhất một sản phẩm", Toast.LENGTH_SHORT).show();
                return;
            }
            Intent intent = new Intent(requireContext(), OrderCreationActivity.class);
            intent.putParcelableArrayListExtra("selectedItems", new ArrayList<>(selectedItems));
            startActivity(intent);
        });
    }

    private void fetchCartItems() {
        TokenManager tokenManager = new TokenManager(requireContext());
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
        } else {
            Toast.makeText(requireContext(), "No token found. Please login.", Toast.LENGTH_SHORT).show();
        }
    }
}