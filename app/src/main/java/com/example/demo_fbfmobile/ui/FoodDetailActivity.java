package com.example.demo_fbfmobile.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
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
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.database.DatabaseHelper;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemRequest;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.model.FoodSizeDto;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.text.NumberFormat;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.graphics.Paint;


public class FoodDetailActivity extends AppCompatActivity {

    public static final String EXTRA_FOOD_ID = "extra_food_id";

    private ImageView ivFoodImage, likefood;

    private Long userId;
    private TextView tvFoodName, tvDescription, tvOriginalPrice, tvDiscountedPrice, tvStock, tvQuantity;
    private RadioGroup rgSizes;
    private Button  btnAddToCart;
    private ImageView btnDecrease, btnIncrease;
    private int selectedQuantity = 1;
    private boolean isLiked;
    private FoodDto currentFood;
    private FoodSizeDto currentSize;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        ivFoodImage = findViewById(R.id.ivFoodImage);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvDescription = findViewById(R.id.tvDescription);
        tvOriginalPrice = findViewById(R.id.tvOriginalPrice);
        tvDiscountedPrice = findViewById(R.id.tvDiscountedPrice);
        tvStock = findViewById(R.id.tvStock);
        rgSizes = findViewById(R.id.rgSizes);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecrease = findViewById(R.id.btnDecrease);
        btnIncrease = findViewById(R.id.btnIncrease);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        likefood = findViewById(R.id.likefood);

        // setup quantity buttons
        btnDecrease.setOnClickListener(v -> updateQuantity(-1));
        btnIncrease.setOnClickListener(v -> updateQuantity(+1));

        long foodId = getIntent().getLongExtra(EXTRA_FOOD_ID, -1);
        if (foodId < 0) { finish(); return; }

        // Lấy userId từ TokenManager
        TokenManager token = new TokenManager(this);
        String userIdString = token.getCartId();

        // Kiểm tra trạng thái like ban đầu
        if (userIdString != null && !userIdString.isEmpty()) {
            userId = Long.parseLong(userIdString);
            String foodIdSelect = String.valueOf(foodId);
            DatabaseHelper dbHelper = new DatabaseHelper(this);
            boolean isLiked = dbHelper.getLikeStatus(foodIdSelect, userId.toString());
            likefood.setImageResource(isLiked ? R.drawable.like : R.drawable.nolike);
            Log.d("LIKE_STATUS", "Initial like status for foodId: " + foodIdSelect + ", userId: " + userIdString + " is " + isLiked);
        } else {
            Log.d("LIKE_STATUS", "User not logged in, default to nolike");
            likefood.setImageResource(R.drawable.nolike);
        }

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

        likefood.setOnClickListener(v -> {
            // Kiểm tra nếu userId là null hoặc không hợp lệ
            if (userIdString == null || userIdString.isEmpty()) {
                Toast.makeText(FoodDetailActivity.this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
                return;
            }

            // Chuyển đổi userId từ String sang Long
            userId = Long.parseLong(userIdString);

            // Lấy foodId (giả sử bạn đã có foodId)
            String foodIdSelect = String.valueOf(foodId);

            // Tạo đối tượng DatabaseHelper để truy cập cơ sở dữ liệu
            DatabaseHelper dbHelper = new DatabaseHelper(FoodDetailActivity.this);

            // Lấy trạng thái like hiện tại từ cơ sở dữ liệu
            boolean isLiked = dbHelper.getLikeStatus(foodIdSelect, userId.toString());

            // Đảo ngược trạng thái like
            isLiked = !isLiked;

            // Cập nhật lại trạng thái like vào cơ sở dữ liệu
            updateLikeStatus(foodIdSelect, userId.toString(), isLiked);

            // Cập nhật giao diện người dùng (icon like)
            likefood.setImageResource(isLiked ? R.drawable.like : R.drawable.nolike);
        });

    }
    public void updateLikeStatus(String foodId, String userId, boolean isLiked) {
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_LIKED, isLiked ? 1 : 0);

        int rowsAffected = db.update(DatabaseHelper.TABLE_LIKES, values,
                DatabaseHelper.COLUMN_FOOD_ID + " = ? AND " + DatabaseHelper.COLUMN_USER_ID + " = ?",
                new String[]{foodId, userId});
        Log.d("DB_UPDATE", "Rows affected: " + rowsAffected + ", foodId: " + foodId + ", userId: " + userId);

        if (rowsAffected == 0) {
            values.put(DatabaseHelper.COLUMN_FOOD_ID, foodId);
            values.put(DatabaseHelper.COLUMN_USER_ID, userId);
            long insertId = db.insert(DatabaseHelper.TABLE_LIKES, null, values);
            Log.d("DB_INSERT", "Inserted row ID: " + insertId);
        }

        db.close();
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
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double originalPrice = size.getPrice();
        double discountedPrice = originalPrice * (1 - size.getDiscountPercentage() / 100.0);
        double discountAmount = originalPrice - discountedPrice;

        // Nếu giảm ít nhất 1000 VND thì hiển thị cả giá gốc và giá giảm
        if (discountAmount >= 1000) {
            tvOriginalPrice.setVisibility(View.VISIBLE);
            tvDiscountedPrice.setVisibility(View.VISIBLE);

            tvOriginalPrice.setText(nf.format(originalPrice) + " VND");
            tvOriginalPrice.setTextColor(ContextCompat.getColor(this, R.color.yellow_primary));
            tvOriginalPrice.setPaintFlags(tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            tvDiscountedPrice.setText(nf.format(discountedPrice) + " VND (-" + size.getDiscountPercentage() + "%)");
            tvDiscountedPrice.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));
        } else {
            // Nếu giảm không đủ 1000 VND, chỉ hiển thị giá gốc như là giá chính
            tvOriginalPrice.setVisibility(View.GONE);  // Ẩn giá gốc
            tvDiscountedPrice.setVisibility(View.VISIBLE);
            tvDiscountedPrice.setText(nf.format(originalPrice) + " VND");
            tvDiscountedPrice.setTextColor(ContextCompat.getColor(this, R.color.yellow_primary));
        }

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
