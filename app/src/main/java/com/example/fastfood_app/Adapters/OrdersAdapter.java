package com.example.fastfood_app.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Activities.AddToCartActivity;
import com.example.fastfood_app.Activities.CartActivity;
import com.example.fastfood_app.Activities.MapActivity;
import com.example.fastfood_app.Activities.OrderItemsActivity;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.User;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ItemsHolderBinding;
import com.example.fastfood_app.databinding.OrdersHolderBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.ViewHolder> {

    FirebaseFirestore firestore;
    Context context;
    List<Order> list;

    public OrdersAdapter(Context context, List<Order> list) {
        this.context = context;
        this.list = list;

        firestore = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.orders_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Order order = list.get(position);

        holder.binding.total.setText(String.valueOf(order.getTotal()));
        firestore.collection("users").document(order.getUserId())
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);

                holder.binding.username.setText(user.getUsername());
                holder.binding.phoneNumber.setText(user.getPhoneNumber());
                Glide.with(context).load(user.getImageUrl())
                        .placeholder(R.drawable.avatar)
                        .into(holder.binding.image);

            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OrderItemsActivity.class);
                intent.putExtra("orderId", order.getId());
                context.startActivity(intent);
            }
        });

        holder.binding.loationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, MapActivity.class);
                intent.putExtra("latitude", order.getLatitide());
                intent.putExtra("longitude", order.getLongitude());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        OrdersHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = OrdersHolderBinding.bind(itemView);
        }
    }

}
