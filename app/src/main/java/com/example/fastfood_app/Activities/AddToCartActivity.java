package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class AddToCartActivity extends AppCompatActivity {

    ActivityAddToCartBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    Item item;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddToCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

        item = (Item) getIntent().getSerializableExtra("item");

        binding.itemname.setText(item.getItemname());
        binding.description.setText(item.getDescription());
        binding.price.setText(String.valueOf(item.getPrice()));
        Glide.with(this).load(item.getImageUrl())
                .placeholder(R.drawable.avatar)
                .into(binding.image);

        firestore.collection("cart")
                .whereEqualTo("userId", auth.getUid())
                .whereEqualTo("itemId", item.getId())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for(QueryDocumentSnapshot queryDocumentSnapshot: querySnapshot){
                    Cart cart = queryDocumentSnapshot.toObject(Cart.class);
                    binding.qty.setText(String.valueOf(cart.getQty()));

                    binding.addToCartBtn.setText("Update Cart");
                }
            }
        });
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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
                if(auth.getCurrentUser() != null) {
                    int qty = Integer.valueOf(binding.qty.getText().toString());
                    if (qty > 0) {
                        if(binding.addToCartBtn.getText().toString().toLowerCase().equals("add to cart")) {
                            Cart cart = new Cart();
                            cart.setUserId(auth.getUid());
                            cart.setItemId(item.getId());
                            cart.setItemname(item.getItemname());
                            cart.setImageUrl(item.getImageUrl());
                            cart.setPrice(item.getPrice());
                            cart.setQty(qty);

                            DocumentReference reference = firestore.collection("cart").document();
                            cart.setId(reference.getId());

                            reference.set(cart)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Intent intent = new Intent(AddToCartActivity.this, CartActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                        }else {
                            firestore.collection("cart")
                                    .whereEqualTo("userId", auth.getUid())
                                    .whereEqualTo("itemId", item.getId())
                                    .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                @Override
                                public void onSuccess(QuerySnapshot querySnapshot) {
                                    for (QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                                        Cart cart = queryDocumentSnapshot.toObject(Cart.class);

                                        Map<String, Object> map = new HashMap<>();
                                        map.put("qty", qty);
                                        firestore.collection("cart").document(cart.getId())
                                                .update(map)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    Intent intent = new Intent(AddToCartActivity.this, CartActivity.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    } else {
                        Toast.makeText(AddToCartActivity.this, "Please add qty", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(AddToCartActivity.this, PhoneNumberActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}