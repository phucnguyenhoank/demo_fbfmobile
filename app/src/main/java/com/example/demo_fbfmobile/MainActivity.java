package com.example.demo_fbfmobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.adapter.FoodAdapter;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.PageResponse;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.ui.FoodDetailActivity;
import com.example.demo_fbfmobile.ui.HomeActivity;
import com.example.demo_fbfmobile.ui.LoginActivity;
import com.example.demo_fbfmobile.ui.RegisterActivity;
import com.example.demo_fbfmobile.ui.ResetPasswordActivity;
import com.example.demo_fbfmobile.utils.TokenManager;
import com.google.android.material.snackbar.Snackbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rvFoods;
    private FoodAdapter adapter;
    private ApiService api;
    private boolean isLowToHigh = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvFoods = findViewById(R.id.rvFoods);
        adapter = new FoodAdapter();
        rvFoods.setAdapter(adapter);
        rvFoods.setLayoutManager(new LinearLayoutManager(this));

        // khi nhấn +
        adapter.setOnAddClickListener(food -> {
            TokenManager tokenManager = new TokenManager(MainActivity.this);
            if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        api = ApiClient.getClient().create(ApiService.class);
        fetchFoods(0, 10, "name,asc");

    }

    // Phương thức xử lý click
    public void onSortClick(View view) {
        isLowToHigh = !isLowToHigh; // Chuyển đổi trạng thái
        updateSortText();
    }

    private void updateSortText() {
        TextView tvSort = findViewById(R.id.tvSort);
        String prefix = "Xếp theo giá: ";
        String suffix = isLowToHigh ? "từ thấp đến cao" : "từ cao đến thấp";
        String fullText = prefix + suffix;

        // Tạo SpannableString để định dạng màu
        SpannableString spannable = new SpannableString(fullText);
        // Màu đen cho "Xếp theo giá: "
        spannable.setSpan(new ForegroundColorSpan(Color.BLACK), 0, prefix.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // Màu orange_primary cho phần còn lại
        spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.orange_primary)),
                prefix.length(), fullText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSort.setText(spannable);
    }

    // Phương thức xử lý click cho ivCart
    public void onCartClick(View view) {
        Toast.makeText(this, "Cart clicked!", Toast.LENGTH_SHORT).show();
        // Thêm logic của bạn, ví dụ: mở màn hình giỏ hàng
        TokenManager tokenManager = new TokenManager(MainActivity.this);
        if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
            // Token valid, go to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Token missing or expired, go to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    // Phương thức xử lý click cho ivProfile
    public void onProfileClick(View view) {
        Toast.makeText(this, "Profile clicked!", Toast.LENGTH_SHORT).show();
        // Thêm logic của bạn, ví dụ: mở màn hình hồ sơ
        TokenManager tokenManager = new TokenManager(MainActivity.this);
        if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
            // Token valid, go to HomeActivity
            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
        } else {
            // Token missing or expired, go to LoginActivity
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        }
    }

    private void fetchFoods(int page, int size, String sort) {
        api.getAllFoods(page, size, sort)
            .enqueue(new Callback<>() {
                @Override public void onResponse(Call<PageResponse<FoodDto>> call,
                                                 Response<PageResponse<FoodDto>> res) {
                    if (res.isSuccessful() && res.body()!=null) {
                        adapter.setData(res.body().getContent());
                    } else {
                        Toast.makeText(MainActivity.this,
                                "Fetch failed: " + res.code(), Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<PageResponse<FoodDto>> call, Throwable t) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Lỗi mạng")
                            .setMessage("Không thể tải danh sách món ăn.\nVui lòng kiểm tra kết nối mạng của bạn.")
                            .setPositiveButton("Thử lại", (dialog, which) -> {
                                // Gọi lại API
                                fetchFoods(0, 10, "name,asc");
                            })
                            .setCancelable(false) // Không cho tắt bằng cách nhấn ngoài
                            .show();
                }
            });
    }
}