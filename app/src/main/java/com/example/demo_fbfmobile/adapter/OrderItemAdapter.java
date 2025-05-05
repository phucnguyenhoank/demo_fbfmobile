package com.example.demo_fbfmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.CartItemDisplay;

import java.util.List;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {
    private List<CartItemDisplay> items;

    public OrderItemAdapter(List<CartItemDisplay> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_creation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItemDisplay item = items.get(position);

        // Bind data
        holder.textFoodName.setText(item.getFoodName());
        holder.textSize.setText("Kích thước: " + item.getSize());
        holder.textQuantity.setText("Số lượng: " + item.getQuantity());

        // Tính giá đã giảm
        double originalPrice = item.getPrice();
        double discountPercentage = item.getDiscountPercentage();
        double discountedPrice = originalPrice * (1 - discountPercentage / 100);

        holder.textOriginalPrice.setText("Giá gốc: " + String.format("%.2f VND", originalPrice));
        holder.textDiscountedPrice.setText("Giá giảm: " + String.format("%.2f VND", discountedPrice));

        // Load hình ảnh
        Glide.with(holder.imageFood.getContext())
                .load(item.getFoodImageUrl())
                .into(holder.imageFood);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFood;
        TextView textFoodName, textSize, textQuantity, textOriginalPrice, textDiscountedPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageFood = itemView.findViewById(R.id.imageFood);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            textSize = itemView.findViewById(R.id.textSize);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            textOriginalPrice = itemView.findViewById(R.id.textOriginalPrice);
            textDiscountedPrice = itemView.findViewById(R.id.textDiscountedPrice);
        }
    }
}