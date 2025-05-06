package com.example.demo_fbfmobile.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.FoodSizeDto;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD_ID = "extra_food_id";

    private ImageView ivFoodImage;
    private TextView tvFoodName, tvDescription, tvOriginalPrice, tvDiscountedPrice, tvStock, tvQuantity;
    private RadioGroup rgSizes;
    private Button btnDecrease, btnIncrease, btnAddToCart;
    private int selectedQuantity = 1;

    private FoodDto currentFood;
    private FoodSizeDto currentSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        ivFoodImage       = findViewById(R.id.ivFoodImage);
        tvFoodName        = findViewById(R.id.tvFoodName);
        tvDescription     = findViewById(R.id.tvDescription);
        tvOriginalPrice   = findViewById(R.id.tvOriginalPrice);
        tvDiscountedPrice = findViewById(R.id.tvDiscountedPrice);
        tvStock           = findViewById(R.id.tvStock);
        rgSizes           = findViewById(R.id.rgSizes);
        tvQuantity        = findViewById(R.id.tvQuantity);
        btnDecrease       = findViewById(R.id.btnDecrease);
        btnIncrease       = findViewById(R.id.btnIncrease);
        btnAddToCart      = findViewById(R.id.btnAddToCart);

        // setup quantity buttons
        btnDecrease.setOnClickListener(v -> updateQuantity(-1));
        btnIncrease.setOnClickListener(v -> updateQuantity(+1));

        long foodId = getIntent().getLongExtra(EXTRA_FOOD_ID, -1);
        if (foodId < 0) { finish(); return; }

        fetchFoodDetail(foodId);

        rgSizes.setOnCheckedChangeListener((group, checkedId) -> {
            if (currentFood == null) return;
            for (FoodSizeDto size : currentFood.getSizes()) {
                if ((size.getSize().equals("S") && checkedId == R.id.rbSizeS) ||
                        (size.getSize().equals("M") && checkedId == R.id.rbSizeM) ||
                        (size.getSize().equals("L") && checkedId == R.id.rbSizeL) ||
                        (size.getSize().equals("XL") && checkedId == R.id.rbSizeXL)) {
                    currentSize = size;
                    updatePriceAndStock(size);
                    break;
                }
            }
        });

        btnAddToCart.setOnClickListener(v -> addToCart());

        Button btnGoToCart = findViewById(R.id.btnGoToCart);
        btnGoToCart.setOnClickListener(v -> {
            Intent intent = new Intent(FoodDetailActivity.this, CartActivity.class);
            startActivity(intent);
        });
    }

    private void updateQuantity(int delta) {
        int newQty = selectedQuantity + delta;
        if (newQty < 1) return;
        if (currentSize != null && newQty > currentSize.getStock()) {
            Toast.makeText(this, "Vượt quá tồn kho", Toast.LENGTH_SHORT).show();
            return;
        }
        selectedQuantity = newQty;
        tvQuantity.setText(String.valueOf(selectedQuantity));
    }

    private void fetchFoodDetail(long id) {
        ApiService api = ApiClient.getApiService();
        api.getFoodDetailsById(id).enqueue(new Callback<>() {
            @Override public void onResponse(Call<ApiResponse<FoodDto>> call, Response<ApiResponse<FoodDto>> res) {
                if (res.isSuccessful() && res.body()!=null && res.body().isSuccess()) {
                    currentFood = res.body().getData();
                    bindData(currentFood);
                } else { showError("Lỗi tải thông tin món ăn"); }
            }
            @Override public void onFailure(Call<ApiResponse<FoodDto>> call, Throwable t) {
                showError("Vui lòng kiểm tra kết nối mạng");
            }
        });
    }

    private void bindData(FoodDto food) {
        tvFoodName.setText(food.getName());
        tvDescription.setText(food.getDescription());
        Glide.with(this).load(food.getImageUrl()).into(ivFoodImage);
        if (!food.getSizes().isEmpty()) {
            rgSizes.check(getRadioButtonIdBySize(food.getSizes().get(0).getSize()));
        }
    }

    private int getRadioButtonIdBySize(String size) {
        switch (size) {
            case "S": return R.id.rbSizeS;
            case "M": return R.id.rbSizeM;
            case "L": return R.id.rbSizeL;
            case "XL": return R.id.rbSizeXL;
            default: return -1;
        }
    }

    private void updatePriceAndStock(FoodSizeDto size) {
        double orig = size.getPrice() / (1 - size.getDiscountPercentage() / 100);
        tvOriginalPrice.setText(String.format("%.2f", orig));
        tvDiscountedPrice.setText(String.format("%.2f", size.getPrice()));
        tvStock.setText("Còn lại: " + size.getStock());
        currentSize = size;
        selectedQuantity = 1;
        tvQuantity.setText("1");
    }

    private void addToCart() {
        if (currentSize == null) { Toast.makeText(this, "Chưa chọn size", Toast.LENGTH_SHORT).show(); return; }
        TokenManager tm = new TokenManager(this);
        String token = tm.getToken();
        if (token == null) { showError("Vui lòng đăng nhập"); return; }

        CartItemRequest req = new CartItemRequest(currentSize.getId(), selectedQuantity);
        Log.i("FoodDetailActivity", currentSize.getId() + ";" + selectedQuantity);
        ApiClient.getApiService().addCartItem("Bearer " + token, req)
                .enqueue(new Callback<>() {
                    @Override public void onResponse(Call<ApiResponse<CartItemDto>> call, Response<ApiResponse<CartItemDto>> res) {
                        if (res.isSuccessful() && res.body()!=null && res.body().isSuccess()) {
                            Toast.makeText(FoodDetailActivity.this, "Đã thêm vào giỏ", Toast.LENGTH_SHORT).show();
                        } else { showError("Thêm giỏ thất bại"); }
                    }
                    @Override public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                        showError("Lỗi mạng, vui lòng thử lại");
                    }
                });
    }

    private void showError(String message) {
        new AlertDialog.Builder(this)
                .setTitle("Lỗi")
                .setMessage(message)
                .setPositiveButton("OK", (d, w) -> d.dismiss())
                .setCancelable(false)
                .show();
    }
}
