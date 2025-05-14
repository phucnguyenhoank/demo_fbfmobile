package com.example.demo_fbfmobile.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.CartItemDisplay;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

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
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double originalPrice = item.getPrice();
        double discountedPrice = originalPrice * (1 - item.getDiscountPercentage() / 100);
        double discountAmount = originalPrice - discountedPrice;
        if (discountAmount >= 1000) {
            holder.textOriginalPrice.setVisibility(View.VISIBLE);
            holder.textDiscountedPrice.setVisibility(View.VISIBLE);

            holder.textOriginalPrice.setText(nf.format(originalPrice) + " VND");
//            holder.textOriginalPrice.setTextColor(R.color.yellow_primary);
            holder.textOriginalPrice.setPaintFlags(holder.textOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            holder.textDiscountedPrice.setText(nf.format(discountedPrice) + " VND (-" + item.getDiscountPercentage() + "%)");
//            tvDiscountedPrice.setTextColor(ContextCompat.getColor(this, R.color.orange_primary));

//            holder.textOriginalPrice.setText("Giá gốc: " + String.format("%.2f VND", originalPrice));
//            holder.textDiscountedPrice.setText("Giá giảm: " + String.format("%.2f VND", discountedPrice));

        }
        else {
            // Nếu giảm không đủ 1000 VND, chỉ hiển thị giá gốc như là giá chính
            holder.textOriginalPrice.setVisibility(View.GONE);  // Ẩn giá gốc
            holder.textDiscountedPrice.setVisibility(View.VISIBLE);
            holder.textDiscountedPrice.setText(nf.format(originalPrice) + " VND");
//            tvDiscountedPrice.setTextColor(ContextCompat.getColor(this, R.color.yellow_primary));

        }



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