package com.example.demo_fbfmobile;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demo_fbfmobile.adapter.OnboardingAdapter;
import com.example.demo_fbfmobile.model.OnBoarding;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private CircleIndicator3 circleIndicator3;
    private List<OnBoarding> onBoardingList;
    private Handler handler;
    private Runnable runnable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_onboarding);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        viewPager2 = findViewById(R.id.viewpager2onboarding);
        circleIndicator3 = findViewById(R.id.circle_indicators);
        handler = new Handler();
        onBoardingList = getOnBoardingList();
        OnboardingAdapter adapter = new OnboardingAdapter(onBoardingList);
        viewPager2.setAdapter(adapter);
        circleIndicator3.setViewPager(viewPager2);
        runnable = new Runnable() {
            @Override
            public void run() {
                if(viewPager2.getCurrentItem() == onBoardingList.size()-1){
                    viewPager2.setCurrentItem(0);
                }
                else {
                    viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1);
                }
            }
        };
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                handler.removeCallbacks(runnable);
                if(position < onBoardingList.size()-1){
                    handler.postDelayed(runnable, 3000);
                }
            }
        });
        viewPager2.setPageTransformer(new DepthPageTransformer());


    }
    public List<OnBoarding> getOnBoardingList(){
        List<OnBoarding> onBoardingList = new ArrayList<>();
        onBoardingList.add(new OnBoarding(R.drawable.a_onboaring, getString(R.string.onboarding_a_title), getString(R.string.onboarding_a_desc), getString(R.string.onboarding_a_btndesc), R.drawable.orderforfood));
        onBoardingList.add(new OnBoarding(R.drawable.b_onboarding, getString(R.string.onboarding_b_title), getString(R.string.onboarding_b_desc), getString(R.string.onboarding_a_btndesc), R.drawable.payment));
        onBoardingList.add(new OnBoarding(R.drawable.c_onboarding, getString(R.string.onboarding_c_title), getString(R.string.onboarding_c_desc), getString(R.string.onboarding_c_btndesc), R.drawable.delivery));
        return onBoardingList;
    }
    public class DepthPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1]
                // This page is way off-screen to the left.
                view.setAlpha(0f);
            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setTranslationZ(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);
            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);
                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);
                // Move it behind the left page
                view.setTranslationZ(-1f);
                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
            } else { // (1,+Infinity)
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }
    }
}