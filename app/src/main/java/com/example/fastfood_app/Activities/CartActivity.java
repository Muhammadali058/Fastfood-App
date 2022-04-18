package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.CartAdapter;
import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.OrderItem;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    CartAdapter cartAdapter;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Placing Order...");

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
                    if(HP.cartList.size() > 0)
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
        progressDialog.show();

        Order order = new Order();
        order.setUserId(auth.getUid());
        order.setTotal(Float.valueOf(binding.total.getText().toString()));

        DocumentReference documentReference = firestore.collection("orders").document();
        order.setId(documentReference.getId());
        documentReference.set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    placeOrderItems(order.getId());
                }
            }
        });
    }

    private void placeOrderItems(String orderId){
        Cart cart = HP.cartList.get(0);

        OrderItem orderItem = new OrderItem();
        orderItem.setOrderId(orderId);
        orderItem.setItemname(cart.getItemname());
        orderItem.setPrice(cart.getPrice());
        orderItem.setQty(cart.getQty());
        orderItem.setImageUrl(cart.getImageUrl());

        DocumentReference documentReference = firestore.collection("orderItems").document();
        orderItem.setId(documentReference.getId());
        documentReference.set(orderItem).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    HP.cartList.remove(cart);

                    if(HP.cartList.size() > 0) {
                        placeOrderItems(orderId);
                    }else {
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }
        });
    }
}