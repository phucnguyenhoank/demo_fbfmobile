package com.example.demo_fbfmobile.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
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

import java.util.ArrayList;
import java.util.List;

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
        String[] tabTitles = {"PAID", "PENDING"};
        for (String title : tabTitles) {
            TabLayout.Tab tab = tabLayout.newTab();

            View customTabView = LayoutInflater.from(requireContext()).inflate(R.layout.item_tabintablayout, null);
            TextView textView = customTabView.findViewById(R.id.tab_text);
            textView.setText(title);

            tab.setCustomView(customTabView);
            tabLayout.addTab(tab);
        }

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
        fetchOrderHistoryPaid(0, 20, "createdAt,desc");
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    fetchOrderHistoryPaid(0, 20, "createdAt,desc");
                } else if (position == 1) {
                    fetchOrderHistoryPending(0, 20, "createdAt,desc");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                if (position == 0) {
                    fetchOrderHistoryPaid(0, 20, "createdAt,desc");
                } else if (position == 1) {
                    fetchOrderHistoryPending(0, 20, "createdAt,desc");
                }
            }
        });



        // Thiết lập sự kiện click cho nút back
        ivBack.setOnClickListener(v -> requireActivity().onBackPressed());
    }

    private void fetchOrderHistoryPending(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<PageResponse<FbfOrderDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        Log.d("Pending Oder History",res.body().toString());
                        if (res.isSuccessful() && res.body() != null) {
                            List<FbfOrderDto> data = res.body().getContent();
                            List<FbfOrderDto> dataPending = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                if (data.get(i).getStatus().equalsIgnoreCase("PENDING"))
                                {
                                    dataPending.add(data.get(i));
                                }
                            }
                            adapter.setOrders(dataPending);
                            Log.d("OrderHistoryFragment", "Pending Order history fetched successfully");
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
    private void fetchOrderHistoryPaid(int page, int size, String sort) {
        ApiService api = ApiClient.getApiService();
        api.getOrderHistory("Bearer " + token, page, size, sort)
                .enqueue(new Callback<PageResponse<FbfOrderDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<FbfOrderDto>> call, Response<PageResponse<FbfOrderDto>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            List<FbfOrderDto> data = res.body().getContent();
                            List<FbfOrderDto> dataPaid = new ArrayList<>();
                            for (int i = 0; i < data.size(); i++) {
                                   if (data.get(i).getStatus().equalsIgnoreCase("Paid"))
                                   {
                                       dataPaid.add(data.get(i));
                                   }
                            }
                            adapter.setOrders(dataPaid);
                            Log.d("OrderHistoryFragment", "Paid Order history fetched successfully");
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