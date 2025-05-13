package com.example.demo_fbfmobile.ui;

import android.content.Context;
import android.content.Intent;
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
import com.example.demo_fbfmobile.adapter.FoodAdapter;
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

import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private ApiService api;
    private String currentSortOption = "";
    private int currentPageNum = 0, currentPageSize = 10;
    private double currentMinPrice = 0, currentMaxPrice = 30000;
    private String currentSearchFoodName = "";
    private Long currentSearchCategoryId = 0L;
    private androidx.appcompat.widget.SearchView svFoodName;
    private TextView tvSimpleUserInfo;

    List<LinearLayout> categories;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout category1 = view.findViewById(R.id.category1);
        LinearLayout category2 = view.findViewById(R.id.category2);
        LinearLayout category3 = view.findViewById(R.id.category3);
        LinearLayout category4 = view.findViewById(R.id.category4);
        LinearLayout category5 = view.findViewById(R.id.category5);
        categories = Arrays.asList(category1, category2, category3, category4, category5);

        // Return the root view
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Khởi tạo views
        tvSimpleUserInfo = view.findViewById(R.id.tvSimpleUserInfo);
        svFoodName = view.findViewById(R.id.svFoodName);
        ImageView ivFilter = view.findViewById(R.id.ivFilter);
        ImageView ivProfile = view.findViewById(R.id.ivProfile);
        LinearLayout category1 = view.findViewById(R.id.category1);
        LinearLayout category2 = view.findViewById(R.id.category2);
        LinearLayout category3 = view.findViewById(R.id.category3);
        LinearLayout category4 = view.findViewById(R.id.category4);
        LinearLayout category5 = view.findViewById(R.id.category5);
        rvFoods = view.findViewById(R.id.rvFoods);
        BottomNavigationView bottomNavigationView = view.findViewById(R.id.mybottomnavigation);

        // Thiết lập SearchView
        svFoodName.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                handleSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        EditText searchEditText = svFoodName.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch(searchEditText.getText().toString());
                return true;
            }
            return false;
        });

        // Thiết lập nút filter
        ivFilter.setOnClickListener(v -> showFilterBottomSheet());

        // Thiết lập danh mục
        for (int i = 0; i < categories.size(); i++) {
            final int categoryId = i + 1;
            LinearLayout categoryView = categories.get(i);
            categoryView.setOnClickListener(v -> {
                currentSearchCategoryId = (long) categoryId;
                fetchFoodsByFullFilter(currentPageNum, currentPageSize, currentSortOption, currentMinPrice, currentMaxPrice, currentSearchFoodName, currentSearchCategoryId);
                for (LinearLayout cat : categories) {
                    cat.setBackgroundResource(R.drawable.bg_rounded_category);
                }
                v.setBackgroundResource(R.drawable.bg_rounded_category_selected);
            });
        }

        // Thiết lập RecyclerView
        adapter = new FoodAdapter();
        rvFoods.setAdapter(adapter);
        rvFoods.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Khi nhấn + thêm hàng vào giỏ
        adapter.setOnAddClickListener(food -> {
            TokenManager tokenManager = new TokenManager(requireContext());
            String token = tokenManager.getToken();
            if (token == null) {
                Toast.makeText(requireContext(), "Chưa đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            Long sizeId = food.getSizes().get(0).getId();
            CartItemRequest req = new CartItemRequest(sizeId, 1);

            api.addCartItem("Bearer " + token, req)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartItemDto>> call, Response<ApiResponse<CartItemDto>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            Toast.makeText(requireContext(), "Added to cart: " + response.body().getData().getId(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(requireContext(), "Add failed: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                        Toast.makeText(requireContext(), "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
        });

        // Khởi tạo API
        api = ApiClient.getClient().create(ApiService.class);

        // Gọi các phương thức ban đầu
        callSecuredEndpoint();
        fetchFoodsByFullFilter(0, currentPageSize, "", currentMinPrice, currentMaxPrice, "", 0L);

        // Thiết lập sự kiện click cho ivProfile
        ivProfile.setOnClickListener(this::onProfileClick);
    }

    private void callSecuredEndpoint() {
        TokenManager tokenManager = new TokenManager(requireContext());
        String token = tokenManager.getToken();
        if (token != null) {
            Call<ApiResponse<String>> call = api.getSecuredData("Bearer " + token);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String username = tokenManager.getUsername();
                        String displayText = "Chào buổi sáng, " + username;
                        tvSimpleUserInfo.setText(displayText);
                    } else {
                        tvSimpleUserInfo.setText("❌ Failed to access secured API");
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResponse<String>> call, @NonNull Throwable t) {
                    tvSimpleUserInfo.setText("⚠️ Error: " + t.getMessage());
                }
            });
        } else {
            tvSimpleUserInfo.setText("No token found. Please login.");
        }
    }

    private void fetchFoodsByFullFilter(int page, int size, String sort, double min, double max, String foodName, Long categoryId) {
        api.getFoodByFullFilter(page, size, sort, min, max, foodName, categoryId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PageResponse<FoodDto>> call, Response<PageResponse<FoodDto>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.setData(res.body().getContent());
                        } else {
                            Toast.makeText(requireContext(), "Không lấy được dữ liệu theo giá: " + res.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Lỗi mạng")
                                .setMessage("Không thể tải món ăn theo giá.\nKiểm tra kết nối mạng.")
                                .setPositiveButton("Thử lại", (dialog, which) -> fetchFoodsByFullFilter(page, size, sort, min, max, foodName, categoryId))
                                .setCancelable(false)
                                .show();
                    }
                });
    }

    private void handleSearch(String query) {
        currentSearchFoodName = (query == null || query.trim().isEmpty()) ? "" : query.trim();
        fetchFoodsByFullFilter(currentPageNum, currentPageSize, currentSortOption, currentMinPrice, currentMaxPrice, currentSearchFoodName, currentSearchCategoryId);

        svFoodName.clearFocus();
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(svFoodName.getWindowToken(), 0);
        }
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        Slider priceMinSlider = bottomSheetView.findViewById(R.id.priceMinSlider);
        Slider priceMaxSlider = bottomSheetView.findViewById(R.id.priceMaxSlider);
        RadioGroup radioFieldSort = bottomSheetView.findViewById(R.id.radioFieldSort);
        RadioGroup radioDirectionSort = bottomSheetView.findViewById(R.id.radioDirectionSort);
        Button btnApplyFilter = bottomSheetView.findViewById(R.id.btnApplyFilter);
        Button btnClearFilter = bottomSheetView.findViewById(R.id.btnClearFilter);

        priceMinSlider.setValue((float) currentMinPrice);
        priceMaxSlider.setValue((float) currentMaxPrice);

        if (!currentSortOption.isEmpty()) {
            String[] parts = currentSortOption.split(",");
            if (parts.length == 2) {
                String field = parts[0];
                String direction = parts[1];
                if (field.equals("id")) {
                    radioFieldSort.check(R.id.radioFoodId);
                } else if (field.equals("name")) {
                    radioFieldSort.check(R.id.radioFoodName);
                }
                if (direction.equals("asc")) {
                    radioDirectionSort.check(R.id.radioAscending);
                } else if (direction.equals("desc")) {
                    radioDirectionSort.check(R.id.radioDescending);
                }
            }
        } else {
            radioFieldSort.clearCheck();
            radioDirectionSort.clearCheck();
        }

        btnApplyFilter.setOnClickListener(view -> {
            float minPrice = priceMinSlider.getValue();
            float maxPrice = priceMaxSlider.getValue();
            currentMinPrice = minPrice;
            currentMaxPrice = maxPrice;

            int selectedSortFieldId = radioFieldSort.getCheckedRadioButtonId();
            int selectedDirectionId = radioDirectionSort.getCheckedRadioButtonId();
            if (selectedSortFieldId != -1 && selectedDirectionId != -1) {
                String field = (selectedSortFieldId == R.id.radioFoodId) ? "id" : "name";
                String direction = (selectedDirectionId == R.id.radioAscending) ? "asc" : "desc";
                currentSortOption = field + "," + direction;
            } else {
                currentSortOption = "";
            }

            updateSortText();
            fetchFoodsByFullFilter(0, 10, currentSortOption, minPrice, maxPrice, currentSearchFoodName, currentSearchCategoryId);
            bottomSheetDialog.dismiss();
        });

        btnClearFilter.setOnClickListener(view -> {
            currentSortOption = "";
            currentMinPrice = 0;
            currentMaxPrice = 300000;
            currentSearchFoodName = "";
            currentSearchCategoryId = 0L;

            updateSortText();
            svFoodName.setQuery("", false);
            svFoodName.clearFocus();


            for (LinearLayout cat : categories) {
                cat.setBackgroundResource(R.drawable.bg_rounded_category);
            }

            fetchFoodsByFullFilter(0, 10, "", 0, 300000, "", 0L);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void updateSortText() {
        TextView tvSort = getView().findViewById(R.id.tvSort);
        tvSort.setText(currentSortOption);
    }

    private void onProfileClick(View view) {
        TokenManager tokenManager = new TokenManager(requireContext());
        if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
            startActivity(new Intent(requireContext(), ProfileActivity.class));
        } else {
            startActivity(new Intent(requireContext(), LoginActivity.class));
        }
    }
}