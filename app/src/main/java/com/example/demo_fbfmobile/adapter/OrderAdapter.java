package com.example.demo_fbfmobile.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.FbfOrderDto;

import java.util.ArrayList;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<FbfOrderDto> orders = new ArrayList<>();

    public void setOrders(List<FbfOrderDto> data) {
        orders.clear();
        orders.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FbfOrderDto o = orders.get(position);
        holder.tvOrderId.setText("Mã hóa đơn #" + o.getId());
        holder.tvOrderDate.setText("Ngày tạo: " + o.getCreatedAt().replace("T", " ").substring(0,16));
        holder.tvTotalPrice.setText("Tổng: " + o.getDiscountedTotalPrice() + " VND");
        holder.tvStatus.setText("Trạng thái: " + o.getStatus());
    }

    @Override public int getItemCount() { return orders.size(); }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvTotalPrice, tvStatus;
        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId    = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate  = itemView.findViewById(R.id.tvOrderDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}

