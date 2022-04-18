package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.fastfood_app.Adapters.OrderItemsAdapter;
import com.example.fastfood_app.Adapters.OrdersAdapter;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.Models.OrderItem;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityOrderItemsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
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
    }
}