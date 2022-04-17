package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.CartAdapter;
import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityCartBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    FirebaseAuth auth;
    CartAdapter cartAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        auth = FirebaseAuth.getInstance();

        cartAdapter = new CartAdapter(this, new CartAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                updateTotal();
            }
        });

        binding.recyclerView.setAdapter(cartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateTotal();

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth.getCurrentUser() == null){
                    Intent intent = new Intent(CartActivity.this, PhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    placeOrder();
                }
            }
        });
    }

    private void updateTotal(){
        float subTotal = 0;

        for(Cart cart: HP.cartList){
            subTotal += cart.getQty() * cart.getPrice();
        }

        if(subTotal > 0) {
            binding.subTotal.setText(String.valueOf(subTotal));
            binding.charges.setText(String.valueOf(50));
            binding.total.setText(String.valueOf(subTotal + 50));
        }else {
            binding.subTotal.setText("0");
            binding.charges.setText("0");
            binding.total.setText("0.00");
        }
    }

    private void placeOrder() {
        Toast.makeText(this, "Place Order", Toast.LENGTH_SHORT).show();
    }

}