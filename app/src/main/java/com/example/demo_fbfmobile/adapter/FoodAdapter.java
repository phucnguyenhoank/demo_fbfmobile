package com.example.demo_fbfmobile.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.ui.FoodDetailActivity;

import java.util.ArrayList;
import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    public interface OnAddClickListener { void onAdd(FoodDto food); }
    private final List<FoodDto> list = new ArrayList<>();
    private OnAddClickListener listener;
    public void setOnAddClickListener(OnAddClickListener l) { this.listener = l; }
    public void setData(List<FoodDto> data) {
        list.clear(); list.addAll(data); notifyDataSetChanged();
    }
    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(v);
    }
    @Override public void onBindViewHolder(@NonNull FoodViewHolder h, int pos) {
        FoodDto f = list.get(pos);
        h.tvName.setText(f.getName());

        double price = f.getSizes().isEmpty() ? 0 : f.getSizes().get(0).getPrice();
        h.tvPrice.setText(String.format("%.2f VND", price));

        Glide.with(h.itemView).load(f.getImageUrl()).into(h.ivFood);

        h.btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onAdd(f);
        });

        h.itemView.setOnClickListener(v -> {
            // Mở FoodDetailActivity và truyền foodId
            Context context = h.itemView.getContext();
            Intent intent = new Intent(context, FoodDetailActivity.class);
            intent.putExtra(FoodDetailActivity.EXTRA_FOOD_ID, f.getId());
            context.startActivity(intent);
        });
    }
    @Override public int getItemCount() { return list.size(); }
    static class FoodViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFood;
        TextView tvName, tvPrice;
        ImageButton btnAdd;
        FoodViewHolder(View v) {
            super(v);
            ivFood = v.findViewById(R.id.ivFood);
            tvName = v.findViewById(R.id.tvFoodName);
            tvPrice = v.findViewById(R.id.tvFoodPrice);
            btnAdd = v.findViewById(R.id.btnAdd);
        }
    }
}
