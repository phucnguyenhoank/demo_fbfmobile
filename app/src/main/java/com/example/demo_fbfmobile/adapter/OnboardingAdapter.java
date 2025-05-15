package com.example.demo_fbfmobile.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.demo_fbfmobile.MainActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.OnBoarding;
import com.example.demo_fbfmobile.ui.HomeFragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.OnboardingViewHolder> {
    private List<OnBoarding> onBoardingList;
    private ViewPager2 viewPager2;
    public OnboardingAdapter(List<OnBoarding> onBoardingList, ViewPager2 viewPager2) {
        this.onBoardingList = onBoardingList;
        this.viewPager2 = viewPager2;
    }
    public List<OnBoarding> getOnBoardingList() {
        return onBoardingList;
    }

    @NonNull
    @Override
    public OnboardingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_onboarding, parent, false);
        return new OnboardingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OnboardingViewHolder holder, int position) {
        OnBoarding onBoarding = onBoardingList.get(position);
        if (onBoarding == null){
            return;
        }else {
            holder.background.setImageResource(onBoarding.getBackgroudId());
            holder.logo.setImageResource(onBoarding.getLogoId());
            holder.title.setText(onBoarding.getTitle());
            holder.description.setText(onBoarding.getDescription());
            holder.btnaction.setText(onBoarding.getDescbtn());
        }
    }

    @Override
    public int getItemCount() {
       if (onBoardingList!=null){
           return onBoardingList.size();
       }
       else {
           return 0;
       }
    }



    public class OnboardingViewHolder extends  RecyclerView.ViewHolder{
        private ImageView background;
        private ImageView logo;
        private ImageView btnnextarrow;
        private TextView txtnextarrow;
        private TextView title;
        private TextView description;
        private Button btnaction;

        public OnboardingViewHolder(@NotNull View itemView) {
            super(itemView);
            background = itemView.findViewById(R.id.backgroud);
            btnnextarrow = itemView.findViewById(R.id.btnnextarrow);
            txtnextarrow = itemView.findViewById(R.id.txtnextarrow);
            logo = itemView.findViewById(R.id.imlogo);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            btnaction = itemView.findViewById(R.id.btn_onboarding);
            btnaction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (btnaction.getText().equals("Get Started")) {
                        Intent intent = new Intent(view.getContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        view.getContext().startActivity(intent);
                    }
                    else {
                        int nextPosition = getAdapterPosition() + 1;
                        if (nextPosition < onBoardingList.size()) {
                            viewPager2.setCurrentItem(nextPosition, true);
                        }
                    }
                }
            });
            btnnextarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });
            txtnextarrow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    view.getContext().startActivity(intent);
                }
            });

        }
    }
}
