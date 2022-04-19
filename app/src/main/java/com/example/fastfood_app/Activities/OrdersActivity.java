package com.example.fastfood_app.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;

import com.example.fastfood_app.Adapters.ItemsAdapter;
import com.example.fastfood_app.Adapters.OrdersAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Order;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityOrdersBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.grpc.Server;

public class OrdersActivity extends AppCompatActivity {

    ActivityOrdersBinding binding;
    List<Order> list;
    OrdersAdapter ordersAdapter;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

//        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
//                .setPersistenceEnabled(false)
//                .build();
//        firestore.setFirestoreSettings(settings);

        list = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, list);
        binding.recyclerView.setAdapter(ordersAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if(auth.getCurrentUser() != null) {
            if(HP.user.getUserType() == 1) {
                firestore.collection("orders").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        list.clear();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            list.add(order);
                        }

                        binding.totalOrders.setText(String.valueOf(list.size()));
                        ordersAdapter.notifyDataSetChanged();
                    }
                });
            }else if(HP.user.getUserType() == 2) {
                firestore.collection("orders").whereEqualTo("userId", auth.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        list.clear();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                            Order order = queryDocumentSnapshot.toObject(Order.class);
                            list.add(order);
                        }

                        binding.totalOrders.setText(String.valueOf(list.size()));
                        ordersAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

}