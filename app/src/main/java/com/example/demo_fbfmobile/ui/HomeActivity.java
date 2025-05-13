package com.example.demo_fbfmobile.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
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

public class HomeActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private ApiService api;
    private String currentSortOption = "";
    private int currentPageNum = 0, currentPageSize = 10;
    private double currentMinPrice = 0, currentMaxPrice = 30000;
    private String currentSearchFoodName = "";
    private Long currentSearchCategoryId = 0L;
    private SearchView svFoodName;

    private TextView tvSimpleUserInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tvSimpleUserInfo = findViewById(R.id.tvSimpleUserInfo);

        svFoodName = findViewById(R.id.svFoodName);
        svFoodName.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
        // Ép xử lý khi nhấn phím Enter, kể cả khi không nhập gì
        EditText searchEditText = svFoodName.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                handleSearch(searchEditText.getText().toString());
                return true;
            }
            return false;
        });

        ImageView ivFilter = findViewById(R.id.ivFilter);
        ivFilter.setOnClickListener(v -> showFilterBottomSheet());

        LinearLayout category1 = findViewById(R.id.category1);
        LinearLayout category2 = findViewById(R.id.category2);
        LinearLayout category3 = findViewById(R.id.category3);
        LinearLayout category4 = findViewById(R.id.category4);
        LinearLayout category5 = findViewById(R.id.category5);
        List<LinearLayout> categories = Arrays.asList(category1, category2, category3, category4, category5);

        for (int i = 0; i < categories.size(); i++) {
            final int categoryId = i + 1; // ID bắt đầu từ 1
            LinearLayout categoryView = categories.get(i);

            categoryView.setOnClickListener(v -> {
                currentSearchCategoryId = (long) categoryId;
                fetchFoodsByFullFilter(currentPageNum, currentPageSize, currentSortOption, currentMinPrice, currentMaxPrice, currentSearchFoodName, currentSearchCategoryId);
                for (LinearLayout cat : categories) {
                    cat.setBackgroundResource(R.drawable.bg_rounded_category); // reset
                }
                v.setBackgroundResource(R.drawable.bg_rounded_category_selected); // chọn
            });
        }

        rvFoods = findViewById(R.id.rvFoods);
        adapter = new FoodAdapter();
        rvFoods.setAdapter(adapter);
        rvFoods.setLayoutManager(new LinearLayoutManager(this));

        // Khi nhấn +
        adapter.setOnAddClickListener(food -> {
            TokenManager tokenManager = new TokenManager(HomeActivity.this);
            if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        api = ApiClient.getClient().create(ApiService.class);

        callSecuredEndpoint();

        fetchFoodsByFullFilter(0, currentPageSize, "", currentMinPrice, currentMaxPrice, "", 0L);

        BottomNavigationView bottomNavigationView = findViewById(R.id.mybottomnavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.menuhome) {
                TokenManager tokenManager = new TokenManager(HomeActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    recreate();
                } else {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (id == R.id.menucart) {
                TokenManager tokenManager = new TokenManager(HomeActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (id == R.id.menufavorite) {
                Toast.makeText(this, "Favorites", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.menuorder) {
                TokenManager tokenManager = new TokenManager(HomeActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    Intent intent = new Intent(HomeActivity.this, OrderHistoryActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                return true;
            } else if (id == R.id.menuhelp) {
                Toast.makeText(this, "Help", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });

    }

    private void callSecuredEndpoint() {
        TokenManager tokenManager = new TokenManager(this);
        String token = tokenManager.getToken();
        if (token != null) {
            Call<ApiResponse<String>> call = api.getSecuredData("Bearer " + token);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ApiResponse<String>> call, @NonNull Response<ApiResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String securedData = response.body().getData();
                        String username = tokenManager.getUsername();
                        boolean expired = tokenManager.isTokenExpired();
                        String displayText = "Chào buổi sáng, " + username; //  + "\nUser: " + username + "\nExpired: " + expired;
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
        }
        else {
            tvSimpleUserInfo.setText("No token found. Please login.");
        }
    }

    private void fetchAllFoods(int page, int size, String sort) {
        api.getAllFoods(page, size, sort)
                .enqueue(new Callback<PageResponse<FoodDto>>() {
                    @Override
                    public void onResponse(Call<PageResponse<FoodDto>> call, Response<PageResponse<FoodDto>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.setData(res.body().getContent());
                        } else {
                            Toast.makeText(HomeActivity.this, "Fetch failed: " + res.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                        Toast.makeText(HomeActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleSearch(String query) {
        currentSearchFoodName = (query == null || query.trim().isEmpty()) ? "" : query.trim();
        fetchFoodsByFullFilter(currentPageNum, currentPageSize, currentSortOption, currentMinPrice, currentMaxPrice, currentSearchFoodName, currentSearchCategoryId);

        svFoodName.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(svFoodName.getWindowToken(), 0);
        }
    }

    private void showFilterBottomSheet() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.filter_bottom_sheet, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        // Khởi tạo các view trong bottom sheet
        Slider priceMinSlider = bottomSheetView.findViewById(R.id.priceMinSlider);
        Slider priceMaxSlider = bottomSheetView.findViewById(R.id.priceMaxSlider);
        RadioGroup radioFieldSort = bottomSheetView.findViewById(R.id.radioFieldSort);
        RadioGroup radioDirectionSort = bottomSheetView.findViewById(R.id.radioDirectionSort);
        Button btnApplyFilter = bottomSheetView.findViewById(R.id.btnApplyFilter);
        Button btnClearFilter = bottomSheetView.findViewById(R.id.btnClearFilter);

        // Đặt giá trị hiện tại cho slider
        priceMinSlider.setValue((float) currentMinPrice);
        priceMaxSlider.setValue((float) currentMaxPrice);

        // Đặt giá trị hiện tại cho radio buttons
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

        // Xử lý sự kiện nút Áp dụng
        btnApplyFilter.setOnClickListener(view -> {
            float minPrice = priceMinSlider.getValue();
            float maxPrice = priceMaxSlider.getValue();

            // Lưu lại để lần sau còn hiển thị đúng
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

        // Xử lý sự kiện nút Xóa bộ lọc
        btnClearFilter.setOnClickListener(view -> {
            // Đặt lại tất cả các biến lọc về giá trị mặc định
            currentSortOption = "";
            currentMinPrice = 0;
            currentMaxPrice = 300000;
            currentSearchFoodName = "";
            currentSearchCategoryId = 0L;

            // Cập nhật UI
            updateSortText();
            svFoodName.setQuery("", false);
            svFoodName.clearFocus();

            // Đặt lại background của các danh mục
            LinearLayout category1 = findViewById(R.id.category1);
            LinearLayout category2 = findViewById(R.id.category2);
            LinearLayout category3 = findViewById(R.id.category3);
            LinearLayout category4 = findViewById(R.id.category4);
            LinearLayout category5 = findViewById(R.id.category5);
            List<LinearLayout> categories = Arrays.asList(category1, category2, category3, category4, category5);
            for (LinearLayout cat : categories) {
                cat.setBackgroundResource(R.drawable.bg_rounded_category);
            }

            // Gọi API với các giá trị mặc định
            fetchFoodsByFullFilter(0, 10, "", 0, 300000, "", 0L);
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void fetchFoodsByFullFilter(int page, int size, String sort, double min, double max, String foodName, Long categoryId) {
        Log.i("HomeActivity", "p:" + page + ";s:" + size + ";so:" + sort + ";min:" + min + ";max:" + max + ";foodName:" + foodName + ";categoryId:" + categoryId);
        api.getFoodByFullFilter(page, size, sort, min, max, foodName, categoryId)
                .enqueue(new Callback<>() {
                    @Override
                    public void onResponse(Call<PageResponse<FoodDto>> call, Response<PageResponse<FoodDto>> res) {
                        if (res.isSuccessful() && res.body() != null) {
                            adapter.setData(res.body().getContent());
                        } else {
                            Toast.makeText(HomeActivity.this, "Không lấy được dữ liệu theo giá: " + res.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                        new AlertDialog.Builder(HomeActivity.this)
                                .setTitle("Lỗi mạng")
                                .setMessage("Không thể tải món ăn theo giá.\nKiểm tra kết nối mạng.")
                                .setPositiveButton("Thử lại", (dialog, which) -> {
                                    fetchFoodsByFullFilter(page, size, sort, min, max, foodName, categoryId);
                                })
                                .setCancelable(false)
                                .show();
                    }
                });
    }

    private void updateSortText() {
        Log.d("SortDebug", "sortOption = " + currentSortOption);
        TextView tvSort = findViewById(R.id.tvSort);
        tvSort.setText(currentSortOption);
    }

    public void onProfileClick(View view) {
        TokenManager tokenManager = new TokenManager(HomeActivity.this);
        if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

}