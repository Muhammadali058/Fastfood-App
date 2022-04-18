package com.example.fastfood_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Activities.OrderItemsActivity;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.OrderItem;
import com.example.fastfood_app.Models.User;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.OrderItemsHolderBinding;
import com.example.fastfood_app.databinding.OrdersHolderBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrderItemsAdapter extends RecyclerView.Adapter<OrderItemsAdapter.ViewHolder> {

    Context context;
    List<OrderItem> list;

    public OrderItemsAdapter(Context context, List<OrderItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.order_items_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OrderItem orderItem = list.get(position);

        holder.binding.itemname.setText(orderItem.getItemname());
        holder.binding.price.setText(String.valueOf(orderItem.getPrice()));
        holder.binding.qty.setText(String.valueOf(orderItem.getQty()));
        Glide.with(context).load(orderItem.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.image);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        OrderItemsHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrderItemsHolderBinding.bind(itemView);
        }
    }

}
