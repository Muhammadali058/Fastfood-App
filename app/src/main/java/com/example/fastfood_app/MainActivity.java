package com.example.fastfood_app;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.Adapters.ItemsAdapter;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseFirestore firestore;
    CategoriesAdapter categoriesAdapter;
    ItemsAdapter itemsAdapter;
    List<Category> categoryList;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setCategoriesAdapter();
        setItemsAdapter();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();

        binding.searchTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItems();
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.backBtn.setVisibility(View.GONE);
                binding.categoriesLayout.setVisibility(View.VISIBLE);
                binding.searchTB.setText("");
            }
        });
    }

    private void setCategoriesAdapter(){
        categoryList = new ArrayList<>();

        categoriesAdapter = new CategoriesAdapter(this, categoryList, new CategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Category category = categoryList.get(position);
                loadItems(category.getId());
            }
        });

        binding.categoriesRecyclerView.setAdapter(categoriesAdapter);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        firestore.collection("categories").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                categoryList.clear();

                for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                    Category category = queryDocumentSnapshot.toObject(Category.class);
                    categoryList.add(category);
                }

                categoriesAdapter.notifyDataSetChanged();

                Category category = categoryList.get(0);
                loadItems(category.getId());
            }
        });
    }

    private void setItemsAdapter(){
        itemList = new ArrayList<>();

        itemsAdapter = new ItemsAdapter(this, itemList, new ItemsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {

            }
        });

        binding.itemsRecyclerView.setAdapter(itemsAdapter);
        binding.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void loadItems(String categoryId){
        firestore.collection("items")
                .whereEqualTo("categoryId", categoryId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot querySnapshot) {
                        itemList.clear();

                        for(QueryDocumentSnapshot queryDocumentSnapshot:querySnapshot){
                            Item item = queryDocumentSnapshot.toObject(Item.class);
                            itemList.add(item);
                        }

                        itemsAdapter.notifyDataSetChanged();
                    }
                });
    }

    private void searchItems() {
        if(binding.searchTB.getText().length() > 0) {
            String text = binding.searchTB.getText().toString();

            firestore.collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    itemList.clear();

                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        Item item = queryDocumentSnapshot.toObject(Item.class);
                        item.setId(queryDocumentSnapshot.getId());

                        if (item.getItemname().toLowerCase().contains(text.toLowerCase()))
                            itemList.add(item);
                    }

                    itemsAdapter.notifyDataSetChanged();
                    binding.backBtn.setVisibility(View.VISIBLE);
                    binding.categoriesLayout.setVisibility(View.GONE);
                }
            });
        }
    }

}