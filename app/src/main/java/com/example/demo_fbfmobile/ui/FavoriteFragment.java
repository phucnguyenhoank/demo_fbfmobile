package com.example.demo_fbfmobile.ui;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.adapter.FavoriteFoodAdapter;
import com.example.demo_fbfmobile.adapter.FoodAdapter;
import com.example.demo_fbfmobile.database.DatabaseHelper;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.slider.Slider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteFragment extends Fragment {

    private ApiService apiService;
    private DatabaseHelper databaseHelper;
    private  LinearLayout linearLayout;
    private String userId;
    private ApiClient apiClient;
    private RecyclerView recyclerView;
    private FavoriteFoodAdapter favoriteFoodAdapter;
    private List<FoodDto> foodList;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycleFacoriteFood);
        linearLayout = view.findViewById(R.id.giaodienkhikocofavoritefood);
        databaseHelper = new DatabaseHelper(requireContext());
        apiClient = new ApiClient();
        foodList = new ArrayList<>();
        apiService = apiClient.getApiService();
        favoriteFoodAdapter = new FavoriteFoodAdapter(foodList, new FavoriteFoodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {

            }
        });
        loadFavoriteFoods();
    }
    public void loadFavoriteFoods() {
        TokenManager tokenManager = new TokenManager(requireContext());
        String token = tokenManager.getToken();
        userId = tokenManager.getCartId();
        if (token == null) {
            Toast.makeText(requireContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
            requireActivity().onBackPressed();
            return;
        }

        apiService = apiClient.getApiService();
        apiService.getAllFoods(0, 10, "name,asc").enqueue(new Callback<PageResponse<FoodDto>>() {
            @Override
            public void onResponse(Call<PageResponse<FoodDto>> call, Response<PageResponse<FoodDto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //Log.d("Favorite Fragment", "Đã phản hồi: " + response.body().getContent());
                    foodList = response.body().getContent();
                    List<String> likedFoodIds = databaseHelper.getLikedFoodIds(userId); // Lấy danh sách foodId đã thích
                    //Log.d("Favorite Fragment", "Đã chạy được danh sách các food Id đc thích" + likedFoodIds);
                    List<FoodDto> favoriteFoods = new ArrayList<>();
                    for (FoodDto food : foodList) {
                        if (likedFoodIds.contains(food.getId().toString())) {
                            favoriteFoods.add(food);
                        }
                    }
                    //Log.d("FavoriteFoodList", "Danh sách món ăn yêu thích: " + favoriteFoods);
//                    if (favoriteFoods.isEmpty()) {
//                        //Log.d("Favorite Fragment", "Chưa có món ăn yêu thích nào");
//                        //Toast.makeText(requireContext(), "Chưa có món ăn yêu thích nào", Toast.LENGTH_SHORT).show();
//                    }
                    if (favoriteFoods.size() > 0){
                        linearLayout.setVisibility(View.GONE);
                    }
                    favoriteFoodAdapter.setFoodList(favoriteFoods);
                    recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
                    recyclerView.setAdapter(favoriteFoodAdapter);
                    if (favoriteFoodAdapter.isEmpty()) {
                        Log.d("FavoriteFragment", "Adapter rỗng sau khi nạp dữ liệu");
                    } else {
                        Log.d("FavoriteFragment", "Adapter có " + favoriteFoodAdapter.getItemCount() + " phần tử");
                    }
                }
            }

            @Override
            public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                Toast.makeText(requireContext(), "Lỗi khi tải danh sách món ăn yêu thích", Toast.LENGTH_SHORT).show();
            }
        });
    }



}