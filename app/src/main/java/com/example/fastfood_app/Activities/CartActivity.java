package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Adapters.CartAdapter;
import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.OrderItem;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityCartBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    ActivityCartBinding binding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    List<Cart> list;
    CartAdapter cartAdapter;
    ProgressDialog progressDialog;
    double latitude = 0;
    double longitude = 0;

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

        list = new ArrayList<>();
        cartAdapter = new CartAdapter(this, list, new CartAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                updateTotal();
            }
        });

        binding.recyclerView.setAdapter(cartAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore.collection("cart").whereEqualTo("userId", auth.getUid())
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                list.clear();

                for (QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                    Cart cart = queryDocumentSnapshot.toObject(Cart.class);
                    list.add(cart);
                }

                cartAdapter.notifyDataSetChanged();
                updateTotal();
            }
        });

        binding.placeOrderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth.getCurrentUser() == null){
                    Intent intent = new Intent(CartActivity.this, PhoneNumberActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    if(list.size() > 0)
                        placeOrder();
                }
            }
        });

        binding.locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, MapActivity.class);
                startActivityForResult(intent, 123);
            }
        });
    }

    private void updateTotal(){
        float subTotal = 0;

        for(Cart cart: list){
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
        if(latitude != 0 && longitude != 0) {
            progressDialog.show();

            Order order = new Order();
            order.setUserId(auth.getUid());
            order.setTotal(Float.valueOf(binding.total.getText().toString()));
            order.setLatitide(latitude);
            order.setLongitude(longitude);

            DocumentReference documentReference = firestore.collection("orders").document();
            order.setId(documentReference.getId());
            documentReference.set(order).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        placeOrderItems(order.getId());
                    }
                }
            });
        }else {
            Toast.makeText(this, "Please select location for delivery", Toast.LENGTH_LONG).show();
        }
    }

    private void placeOrderItems(String orderId){
        Cart cart = list.get(0);

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
                    firestore.collection("cart").document(cart.getId())
                            .delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        list.remove(cart);

                                        if(list.size() > 0) {
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
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && data != null){
            latitude = data.getDoubleExtra("latitude", 0);
            longitude = data.getDoubleExtra("longitude", 0);
        }
    }

}