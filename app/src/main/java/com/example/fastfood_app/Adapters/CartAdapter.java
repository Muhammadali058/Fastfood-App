package com.example.fastfood_app.Adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Activities.AddToCartActivity;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.CartItemsHolderBinding;
import com.example.fastfood_app.databinding.ItemsHolderBinding;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    OnClickListener onClickListener;

    public CartAdapter(Context context, OnClickListener onClickListener) {
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_items_holder, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Cart cart = HP.cartList.get(position);

        holder.binding.itemname.setText(cart.getItemname());
        holder.binding.price.setText(String.valueOf(cart.getPrice()));
        holder.binding.qty.setText(String.valueOf(cart.getQty()));
        holder.binding.total.setText(String.valueOf(cart.getQty() * cart.getPrice()));
        Glide.with(context).load(cart.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(holder.binding.image);

        holder.binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = cart.getQty();
                qty++;
                cart.setQty(qty);
                onClickListener.onClick(position);
                notifyDataSetChanged();
            }
        });

        holder.binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = cart.getQty();
                if(qty > 0) {
                    qty--;
                    holder.binding.qty.setText(String.valueOf(qty));
                    cart.setQty(qty);
                    onClickListener.onClick(position);
                    notifyDataSetChanged();
                }
            }
        });

        final int pos = position;
        holder.binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setTitle("Remove Item")
                        .setMessage("Are you sure to remove this item from order?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                HP.cartList.remove(cart);
                                onClickListener.onClick(pos);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return HP.cartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        CartItemsHolderBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = CartItemsHolderBinding.bind(itemView);
        }
    }

    public interface OnClickListener{
        void onClick(int position);
    }
}
