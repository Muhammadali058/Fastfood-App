package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.fastfood_app.Adapters.OrderItemsAdapter;
import com.example.fastfood_app.Adapters.OrdersAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Cart;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.OrderItem;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityOrderItemsBinding;
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

public class OrderItemsActivity extends AppCompatActivity {

    ActivityOrderItemsBinding binding;
    List<OrderItem> list;
    OrderItemsAdapter orderItemsAdapter;
    FirebaseFirestore firestore;
    String orderId;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderId = getIntent().getStringExtra("orderId");

        init();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Dispatching...");

        list = new ArrayList<>();
        orderItemsAdapter = new OrderItemsAdapter(this, list);
        binding.recyclerView.setAdapter(orderItemsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        firestore.collection("orderItems").whereEqualTo("orderId", orderId)
                .get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                    OrderItem orderItem = queryDocumentSnapshot.toObject(OrderItem.class);
                    list.add(orderItem);
                }

                binding.totalItems.setText(String.valueOf(list.size()));
                orderItemsAdapter.notifyDataSetChanged();
            }
        });

        if(HP.user.getUserType() == 2){
            binding.dispatchLayout.setVisibility(View.GONE);
        }

        binding.dispatchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog dialog = new AlertDialog.Builder(OrderItemsActivity.this)
                        .setTitle("Order Dispatched")
                        .setMessage("Are you sure this order has been dispatched?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                deleteOrder();
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

    private void deleteOrder() {
        progressDialog.show();

        firestore.collection("orders").document(orderId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    deleteOrderItems();
                }
            }
        });
    }

    private void deleteOrderItems(){
        OrderItem orderItem = list.get(0);

        firestore.collection("orderItems").document(orderItem.getId())
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    list.remove(orderItem);

                    if(list.size() > 0) {
                        deleteOrderItems();
                    }else {
                        progressDialog.dismiss();
                        finish();
                    }
                }
            }
        });
    }
}