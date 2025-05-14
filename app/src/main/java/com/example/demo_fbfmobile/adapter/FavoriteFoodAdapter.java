package com.example.demo_fbfmobile.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.FoodDto;
import com.example.demo_fbfmobile.ui.FoodDetailActivity;

import java.util.List;

public class FavoriteFoodAdapter extends RecyclerView.Adapter<FavoriteFoodAdapter.ViewHolder> {
    private List<FoodDto> favoriteFoods;
    private OnItemClickListener listener;

    public void setFoodList(List<FoodDto> foodList) {
        this.favoriteFoods = foodList;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener{
        void onItemClick();
    }
    public FavoriteFoodAdapter(List<FoodDto> favoriteFoods, OnItemClickListener listener) {
        this.favoriteFoods = favoriteFoods;
        this.listener = listener;
    }
    @NonNull
    @Override
    public FavoriteFoodAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteFoodAdapter.ViewHolder holder, int position) {
        FoodDto foodDto = favoriteFoods.get(position);
        holder.foodId = foodDto.getId();
        holder.favoriteFoodName.setText(foodDto.getName());
        Glide.with(holder.itemView.getContext()).load(foodDto.getImageUrl()).into(holder.imgFavoriteFood);
    }

    @Override
    public int getItemCount() {
        return favoriteFoods.size();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFavoriteFood;
        Long foodId;
        TextView favoriteFoodName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFavoriteFood = itemView.findViewById(R.id.imgFavoriteFood);
            favoriteFoodName = itemView.findViewById(R.id.favoriteFoodName);
            itemView.setOnClickListener(v ->{
                if (listener != null){
                    listener.onItemClick();
                    Intent intent = new Intent(itemView.getContext(), FoodDetailActivity.class);
                    intent.putExtra(FoodDetailActivity.EXTRA_FOOD_ID, foodId);
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }
}
