package com.example.demo_fbfmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.demo_fbfmobile.ui.CartFragment;
import com.example.demo_fbfmobile.ui.FavoriteFragment;
import com.example.demo_fbfmobile.ui.HelpFragment;
import com.example.demo_fbfmobile.ui.HomeActivity;
import com.example.demo_fbfmobile.ui.HomeFragment;
import com.example.demo_fbfmobile.ui.LoginActivity;
import com.example.demo_fbfmobile.ui.OrderHistoryFragment;
import com.example.demo_fbfmobile.utils.TokenManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    private long backPressedTime;
    private Toast backToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Đăng ký callback cho nút back
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) backToast.cancel();
                    finish(); // Thoát ứng dụng
                } else {
                    backToast = Toast.makeText(getApplicationContext(), "Nhấn lần nữa để thoát", Toast.LENGTH_SHORT);
                    backToast.show();
                    backPressedTime = System.currentTimeMillis();
                }
            }
        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                selectedFragment = new HomeFragment();

            } else if (id == R.id.menu_cart) {
                TokenManager tokenManager = new TokenManager(MainActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    selectedFragment = new CartFragment();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            } else if (id == R.id.menu_order_history) {
                TokenManager tokenManager = new TokenManager(MainActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    selectedFragment = new OrderHistoryFragment();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }

            }
            else if (id == R.id.menu_help) {
                selectedFragment = new HelpFragment();
            }
            else if (id == R.id.menu_favorite)
            {
                TokenManager tokenManager = new TokenManager(MainActivity.this);
                if (tokenManager.getToken() != null && !tokenManager.isTokenExpired()) {
                    selectedFragment = new FavoriteFragment();
                } else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }

            if (selectedFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }

            return true;
        });

        // Load default fragment
        if (savedInstanceState == null) {
            bottomNavigationView.setSelectedItemId(R.id.menu_home);
        }

        // Check if coming from another activity with a request to open a fragment
        String openFragment = getIntent().getStringExtra("openFragment");
        if (openFragment != null && openFragment.equals("cart")) {
            bottomNavigationView.setSelectedItemId(R.id.menu_cart);
        }

    }


}