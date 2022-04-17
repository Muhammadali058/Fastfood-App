package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityAddToCartBinding;

public class AddToCartActivity extends AppCompatActivity {

    ActivityAddToCartBinding binding;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddToCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        item = (Item) getIntent().getSerializableExtra("item");

        binding.itemname.setText(item.getItemname());
        binding.description.setText(item.getDescription());
        binding.price.setText(String.valueOf(item.getPrice()));
        Glide.with(this).load(item.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(binding.image);

        for(Cart cart : HP.cartList){
            if(cart.getId().equals(item.getId())){
                binding.qty.setText(String.valueOf(cart.getQty()));
                break;
            }
        }

        init();
    }

    private void init(){
        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(binding.qty.getText().toString());
                qty++;
                binding.qty.setText(String.valueOf(qty));
            }
        });

        binding.minusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(binding.qty.getText().toString());
                if(qty > 0) {
                    qty--;
                    binding.qty.setText(String.valueOf(qty));
                }
            }
        });
        
        binding.addToCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int qty = Integer.valueOf(binding.qty.getText().toString());
                if(qty > 0){
                    Cart cart = new Cart();
                    cart.setId(item.getId());
                    cart.setItemname(item.getItemname());
                    cart.setImageUrl(item.getImageUrl());
                    cart.setPrice(item.getPrice());
                    cart.setQty(qty);

                    HP.cartList.add(cart);

                    Intent intent = new Intent(AddToCartActivity.this, CartActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Toast.makeText(AddToCartActivity.this, "Please add qty", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}