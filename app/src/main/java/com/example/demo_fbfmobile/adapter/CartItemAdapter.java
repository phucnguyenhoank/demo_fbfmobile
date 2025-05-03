package com.example.demo_fbfmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.CartItemDisplay;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<CartItemDisplay> cartItems;

    public CartItemAdapter(List<CartItemDisplay> cartItems) {
        this.cartItems = cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFood;
        TextView textFoodName, textSize, textPrice, textQuantity;

        public ViewHolder(View itemView) {
            super(itemView);
            imageFood = itemView.findViewById(R.id.imageFood);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            textSize = itemView.findViewById(R.id.textSize);
            textPrice = itemView.findViewById(R.id.textPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CartItemDisplay item = cartItems.get(position);
        holder.textFoodName.setText(item.getFoodName());
        holder.textSize.setText("Size: " + item.getSize());
        holder.textPrice.setText("Price: $" + item.getPrice());
        holder.textQuantity.setText("Quantity: " + item.getQuantity());

        Glide.with(holder.imageFood.getContext())
                .load(item.getFoodImageUrl())
                .into(holder.imageFood);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}


