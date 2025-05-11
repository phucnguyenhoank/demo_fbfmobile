package com.example.demo_fbfmobile.ui;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.FbfUserDto;
import com.example.demo_fbfmobile.model.UpdateFbfUserRequest;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.utils.TokenManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    private TextView tvId, tvUsername, tvEmail, tvName, tvRole;
    private EditText etPhone, etAddress;
    private Button btnSave;
    private ApiService apiService;
    private String authToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets sys = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(sys.left, sys.top, sys.right, sys.bottom);
            return insets;
        });

        // Ánh xạ view
        tvId       = findViewById(R.id.tvId);
        tvUsername = findViewById(R.id.tvUsername);
        tvEmail    = findViewById(R.id.tvEmail);
        tvName     = findViewById(R.id.tvName);
        tvRole     = findViewById(R.id.tvRole);
        etPhone    = findViewById(R.id.etPhone);
        etAddress  = findViewById(R.id.etAddress);
        btnSave    = findViewById(R.id.btnSave);

        apiService = ApiClient.getClient().create(ApiService.class);
        String token = new TokenManager(this).getToken();
        authToken = token != null ? "Bearer " + token : "";

        loadCurrentUser();

        btnSave.setOnClickListener(v -> {
            String newPhone   = etPhone.getText().toString().trim();
            String newAddress = etAddress.getText().toString().trim();
            if (newPhone.isEmpty() || newAddress.isEmpty()) {
                Toast.makeText(this, "Phone and address cannot be empty", Toast.LENGTH_SHORT).show();
            } else {
                updateUser(newPhone, newAddress);
            }
        });
    }

    private void loadCurrentUser() {
        apiService.getCurrentUser(authToken).enqueue(new Callback<ApiResponse<FbfUserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfUserDto>> call, Response<ApiResponse<FbfUserDto>> res) {
                if (res.isSuccessful() && res.body() != null && res.body().isSuccess()) {
                    FbfUserDto user = res.body().getData();
                    // Hiển thị các trường
                    tvId.setText(String.valueOf(user.getId()));
                    tvUsername.setText(user.getUsername());
                    tvEmail.setText(user.getEmail());
                    tvName.setText(user.getName());
                    tvRole.setText(user.getFbfRole());
                    // Điền vào EditText
                    etPhone.setText(user.getPhoneNumber());
                    etAddress.setText(user.getAddress());
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to load profile", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<FbfUserDto>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUser(String phone, String address) {
        UpdateFbfUserRequest req = new UpdateFbfUserRequest(phone, address);
        apiService.updateCurrentUser(authToken, req).enqueue(new Callback<ApiResponse<FbfUserDto>>() {
            @Override
            public void onResponse(Call<ApiResponse<FbfUserDto>> call, Response<ApiResponse<FbfUserDto>> res) {
                if (res.isSuccessful() && res.body() != null && res.body().isSuccess()) {
                    Toast.makeText(ProfileActivity.this, "Profile updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Update failed", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<ApiResponse<FbfUserDto>> call, Throwable t) {
                Toast.makeText(ProfileActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
