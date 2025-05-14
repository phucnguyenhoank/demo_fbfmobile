package com.example.demo_fbfmobile.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.demo_fbfmobile.HistoryOrderPaidActivity;
import com.example.demo_fbfmobile.R;
import com.example.demo_fbfmobile.model.FbfOrderDto;
import com.example.demo_fbfmobile.ui.PaymentActivity;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {
    private final List<FbfOrderDto> orders = new ArrayList<>();

    // 1. Interface để lắng nghe sự kiện click
    public interface OnItemClickListener {
        void onItemClick(Long orderId);
    }

    private OnItemClickListener listener;

    // 2. Hàm set listener từ bên ngoài
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setOrders(List<FbfOrderDto> data) {
        orders.clear();
        orders.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        FbfOrderDto o = orders.get(position);
        holder.tvOrderId.setText("Mã hóa đơn #" + o.getId());
        holder.tvOrderDate.setText("Ngày tạo: " + o.getCreatedAt().replace("T", " ").substring(0, 16));
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        double discountPercentage = 0;
        if (o.getDiscountCode() != null) {
            discountPercentage = o.getDiscountCode().getDiscountPercentage();
        }
        if (discountPercentage > 0) {
            holder.tvTotalPrice.setText("Tổng: " + nf.format(o.getDiscountedTotalPrice()) + " VND (-" + discountPercentage + "%)");
        }
        else {
            holder.tvTotalPrice.setText("Tổng: " + nf.format(o.getDiscountedTotalPrice()) + " VND");
        }

        holder.tvStatus.setText(o.getStatus());

        // 3. Gán sự kiện click
        holder.itemView.setOnClickListener(v -> {
            Log.d("Order Adapter", o.getStatus() + " " + o.getId());
            if ("PAID".equalsIgnoreCase(o.getStatus())) {
                Log.d("Order Adapter", "onItemClick Success");
                Intent intent = new Intent(v.getContext(), HistoryOrderPaidActivity.class);
                intent.putExtra("orderId", o.getId());
                v.getContext().startActivity(intent);
            }
            else if("PENDING".equalsIgnoreCase(o.getStatus())){
                Intent intent = new Intent(v.getContext(), PaymentActivity.class);
                intent.putExtra("orderId", o.getId());
                v.getContext().startActivity(intent);
            }
            else {
                Toast.makeText(v.getContext(), "This order was canceled", Toast.LENGTH_SHORT).show();
            }


        });
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvOrderDate, tvTotalPrice, tvStatus;

        OrderViewHolder(View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);
            tvTotalPrice = itemView.findViewById(R.id.tvTotalPrice);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
