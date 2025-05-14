package com.example.demo_fbfmobile.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.CartItemDisplay;
import com.example.demo_fbfmobile.model.CartItemDto;
import com.example.demo_fbfmobile.network.ApiClient;
import com.example.demo_fbfmobile.network.ApiService;
import com.example.demo_fbfmobile.model.ApiResponse;
import com.example.demo_fbfmobile.model.CartItemUpdateRequest;
import com.example.demo_fbfmobile.utils.TokenManager;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.ViewHolder> {
    private List<CartItemDisplay> cartItems;
    private String authToken;
    private Context context;
    private OnItemClickListener listener;

    public CartItemAdapter(List<CartItemDisplay> cartItems, Context context, OnItemClickListener listener) {
        this.cartItems = cartItems;
        this.context = context;
        this.authToken = "Bearer " + new TokenManager(context).getToken();
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        boolean isSpinnerInitialized = false;
        ImageView imageFood;
        TextView textFoodName, textPrice, textQuantity;
        Spinner spinnerSize;
        ImageView btnDecrease, btnIncrease, btnDelete;

        CheckBox checkboxSelect;

        public ViewHolder(View itemView) {
            super(itemView);
            imageFood = itemView.findViewById(R.id.imageFood);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            spinnerSize = itemView.findViewById(R.id.spinnerSize);
            textPrice = itemView.findViewById(R.id.textPrice);
            textQuantity = itemView.findViewById(R.id.textQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
            btnDelete = itemView.findViewById(R.id.btnDelete);
            checkboxSelect = itemView.findViewById(R.id.checkboxSelect);
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick();
                }
            });
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
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));

        double discountPercentage = item.getDiscountPercentage();
        if (discountPercentage > 0){
            double discountedPrice = item.getPrice() * (1 - discountPercentage / 100);
            holder.textPrice.setText("Đơn giá: " + nf.format(discountedPrice) + " VND (-" + discountPercentage + "%)");
        }
        else {
            holder.textPrice.setText("Đơn giá: " + nf.format(item.getPrice()) + " VND");
        }

        holder.textQuantity.setText(item.getQuantity().toString());
        holder.checkboxSelect.setChecked(item.isSelected());
        holder.checkboxSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            item.setSelected(isChecked);
            if ( listener != null){
                listener.onItemClick();
            }
        });


        Glide.with(holder.imageFood.getContext())
                .load(item.getFoodImageUrl())
                .into(holder.imageFood);

        // Set spinner
        List<String> sizeOptions = Arrays.asList("S", "M", "L", "XL");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                android.R.layout.simple_spinner_item, sizeOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinnerSize.setAdapter(adapter);

        int selectedIndex = sizeOptions.indexOf(item.getSize());
        if (selectedIndex >= 0) {
            holder.spinnerSize.setTag("initializing");
            holder.spinnerSize.setSelection(selectedIndex, false);
            holder.spinnerSize.setTag(null);
        }


        // Xử lý nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            CartItemDisplay currentItem = cartItems.get(adapterPosition);
            Long cartItemId = currentItem.getId();

            ApiService apiService = ApiClient.getApiService();
            Call<ApiResponse<String>> call = apiService.deleteCartItem(authToken, cartItemId);
            call.enqueue(new Callback<>() {
                @Override
                public void onResponse(Call<ApiResponse<String>> call, Response<ApiResponse<String>> response) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION) return;

                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        cartItems.remove(currentPosition);
                        notifyItemRemoved(currentPosition);
                        notifyItemRangeChanged(currentPosition, cartItems.size());
                    } else {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<String>> call, Throwable t) {
                    Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // Xử lý nút tăng số lượng
        holder.btnIncrease.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            CartItemDisplay currentItem = cartItems.get(adapterPosition);
            int newQuantity = currentItem.getQuantity() + 1;
            currentItem.setQuantity(newQuantity);
            holder.textQuantity.setText("" + newQuantity);

            CartItemUpdateRequest request = new CartItemUpdateRequest(currentItem.getId(), newQuantity, currentItem.getSize());
            ApiService apiService = ApiClient.getApiService();
            Call<ApiResponse<CartItemDto>> call = apiService.updateCartItem(authToken, request);
            call.enqueue(new Callback<ApiResponse<CartItemDto>>() {
                @Override
                public void onResponse(Call<ApiResponse<CartItemDto>> call, Response<ApiResponse<CartItemDto>> response) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION) return;

                    CartItemDisplay item = cartItems.get(currentPosition);
                    if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                        item.setQuantity(newQuantity - 1);
                        holder.textQuantity.setText("" + (newQuantity - 1));
                        Toast.makeText(context, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                    int currentPosition = holder.getBindingAdapterPosition();
                    if (currentPosition == RecyclerView.NO_POSITION) return;

                    CartItemDisplay item = cartItems.get(currentPosition);
                    item.setQuantity(newQuantity - 1);
                    holder.textQuantity.setText("" + (newQuantity - 1));
                    Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                }
            });
            if (listener != null) {
                listener.onItemClick();
            }
        });

        // Xử lý nút giảm số lượng
        holder.btnDecrease.setOnClickListener(v -> {
            int adapterPosition = holder.getBindingAdapterPosition();
            if (adapterPosition == RecyclerView.NO_POSITION) return;

            CartItemDisplay currentItem = cartItems.get(adapterPosition);
            int currentQuantity = currentItem.getQuantity();
            if (currentQuantity > 1) {
                int newQuantity = currentQuantity - 1;
                currentItem.setQuantity(newQuantity);
                holder.textQuantity.setText("" + newQuantity);

                CartItemUpdateRequest request = new CartItemUpdateRequest(currentItem.getId(), newQuantity, currentItem.getSize());
                ApiService apiService = ApiClient.getApiService();
                Call<ApiResponse<CartItemDto>> call = apiService.updateCartItem(authToken, request);
                call.enqueue(new Callback<ApiResponse<CartItemDto>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<CartItemDto>> call, Response<ApiResponse<CartItemDto>> response) {
                        int currentPosition = holder.getBindingAdapterPosition();
                        if (currentPosition == RecyclerView.NO_POSITION) return;

                        CartItemDisplay item = cartItems.get(currentPosition);
                        if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                            item.setQuantity(currentQuantity);
                            holder.textQuantity.setText("" + currentQuantity);
                            Toast.makeText(context, "Cập nhật số lượng thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                        int currentPosition = holder.getBindingAdapterPosition();
                        if (currentPosition == RecyclerView.NO_POSITION) return;

                        CartItemDisplay item = cartItems.get(currentPosition);
                        item.setQuantity(currentQuantity);
                        holder.textQuantity.setText("" + currentQuantity);
                        Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                    }
                });
                if (listener != null) {
                    listener.onItemClick();
                }
            }
        });

        // Xử lý thay đổi kích thước
        holder.spinnerSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if ("initializing".equals(holder.spinnerSize.getTag())) return;
                int adapterPosition = holder.getBindingAdapterPosition();
                if (adapterPosition == RecyclerView.NO_POSITION) return;

                CartItemDisplay currentItem = cartItems.get(adapterPosition);
                String selectedSize = sizeOptions.get(pos);
                String currentSize = currentItem.getSize();
                if (!selectedSize.equals(currentSize)) {
                    currentItem.setSize(selectedSize);

                    CartItemUpdateRequest request = new CartItemUpdateRequest(currentItem.getId(), currentItem.getQuantity(), selectedSize);
                    ApiService apiService = ApiClient.getApiService();
                    Call<ApiResponse<CartItemDto>> call = apiService.updateCartItem(authToken, request);
                    call.enqueue(new Callback<ApiResponse<CartItemDto>>() {
                        @Override
                        public void onResponse(Call<ApiResponse<CartItemDto>> call, Response<ApiResponse<CartItemDto>> response) {
                            int currentPosition = holder.getBindingAdapterPosition();
                            if (currentPosition == RecyclerView.NO_POSITION) return;
                            CartItemDisplay item = cartItems.get(currentPosition);
                            if (!response.isSuccessful() || response.body() == null || !response.body().isSuccess()) {
                                item.setSize(currentSize);
                                int previousIndex = sizeOptions.indexOf(currentSize);
                                if (previousIndex >= 0) {
                                    holder.spinnerSize.setSelection(previousIndex);
                                }
                                Toast.makeText(context, "Cập nhật kích thước thất bại", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                CartItemDto newCartItemDto = response.body().getData();
                                Double newPrice = newCartItemDto.getPrice();
                                Double newDiscountPercentage = newCartItemDto.getDiscountPercentage(); // Lấy discount mới

                                // Cập nhật price và discountPercentage cho item
                                item.setPrice(newPrice);
                                item.setDiscountPercentage(newDiscountPercentage);
                                item.setQuantity(newCartItemDto.getQuantity());

                                NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
                                if (newDiscountPercentage > 0) {
                                    double discountedPrice = newPrice * (1 - newDiscountPercentage / 100);
                                    holder.textPrice.setText("Đơn giá: " + nf.format(discountedPrice) + " VND (-" + newDiscountPercentage + "%)");
                                } else {
                                    holder.textPrice.setText("Đơn giá: " + nf.format(newPrice) + " VND");
                                }

                                if (listener != null) {
                                    listener.onItemClick();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse<CartItemDto>> call, Throwable t) {
                            int currentPosition = holder.getBindingAdapterPosition();
                            if (currentPosition == RecyclerView.NO_POSITION) return;

                            CartItemDisplay item = cartItems.get(currentPosition);
                            item.setSize(currentSize);
                            int previousIndex = sizeOptions.indexOf(currentSize);
                            if (previousIndex >= 0) {
                                holder.spinnerSize.setSelection(previousIndex);
                            }
                            Toast.makeText(context, "Lỗi mạng", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }
}