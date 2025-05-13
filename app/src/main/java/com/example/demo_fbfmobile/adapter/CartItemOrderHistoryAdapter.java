package com.example.demo_fbfmobile.adapter;

import android.content.Context;
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
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.model.OrderItemDetailDto;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CartItemOrderHistoryAdapter extends RecyclerView.Adapter<CartItemOrderHistoryAdapter.CartItemOrderHistoryViewHolder> {
    private List<OrderItemDetailDto> cartItems;
    private String authToken;
    private Context context;
    public CartItemOrderHistoryAdapter(List<OrderItemDetailDto> cartItems, Context context)
    {
        this.cartItems = cartItems;
        this.context = context;
    }


    @NonNull
    @Override
    public CartItemOrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart_order_history_paid, parent, false);
        return new CartItemOrderHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemOrderHistoryViewHolder holder, int position) {
        OrderItemDetailDto item = cartItems.get(position);
        Glide.with(holder.imageFood.getContext())
                .load(item.getImageUrl())
                .into(holder.imageFood);
        holder.textFoodName.setText(item.getFoodName());
        holder.textPrice.setText("Đơn giá: " + item.getDiscountedPrice() + " VND");
        holder.textQuantity.setText("Số lượng: " + item.getQuantity());
        holder.textSize.setText("Kích thước: " + item.getSize());
        holder.totalPrice.setText("Thành tiền: " + (item.getDiscountedPrice() * item.getQuantity()) + " VND");
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }


    public static class  CartItemOrderHistoryViewHolder extends RecyclerView.ViewHolder {
        ImageView imageFood;
        TextView textFoodName, textPrice, textQuantity, textSize, totalPrice;
        public CartItemOrderHistoryViewHolder(View itemView) {
            super(itemView);
            imageFood = itemView.findViewById(R.id.imageProduct);
            textFoodName = itemView.findViewById(R.id.textProductName);
            textPrice = itemView.findViewById(R.id.textUnitPrice);
            textQuantity = itemView.findViewById(R.id.textQuantityCartItemHistory);
            textSize = itemView.findViewById(R.id.textSizeCartItemHistory);
            totalPrice = itemView.findViewById(R.id.textTotalPrice);
        }
    }
}
