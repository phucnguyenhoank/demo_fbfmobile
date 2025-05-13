package com.example.demo_fbfmobile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class OrderHistoryFragment extends Fragment {

    private RecyclerView rvOrders;
    private OrderAdapter adapter;
    private String token;
    private TabLayout tabLayout;
    private ImageView ivBack;

    public OrderHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo views
        ivBack = view.findViewById(R.id.ivBack);
        tabLayout = view.findViewById(R.id.tabLayout);
        rvOrders = view.findViewById(R.id.rvOrders);

        // Thiết lập TabLayout
        tabLayout.addTab(tabLayout.newTab().setText("Paid"));
        tabLayout.addTab(tabLayout.newTab().setText("Pending"));

        // Thiết lập RecyclerView
        adapter = new OrderAdapter();
        rvOrders.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvOrders.setAdapter(adapter);

        // Lấy token
        TokenManager tokenManager = new TokenManager(requireContext());
        token = tokenManager.getToken();
        if (token == null) {
            Toast.makeText(requireContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        // Lấy lịch sử đơn hàng
        fetchOrderHistory(0, 20, "createdAt,desc");

        // Thiết lập sự kiện click cho nút back
        ivBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void fetchOrderHistory(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<PageResponse<FbfOrderDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.setOrders(res.body().getContent());
                            Log.d("OrderHistoryFragment", "Order history fetched successfully");
                        } else {
                            Toast.makeText(requireContext(), "Lỗi: " + res.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<FbfOrderDto>> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}