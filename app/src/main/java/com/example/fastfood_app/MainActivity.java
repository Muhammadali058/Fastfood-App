package com.example.fastfood_app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.Adapters.ItemsAdapter;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    CategoriesAdapter categoriesAdapter;
    ItemsAdapter itemsAdapter;
    List<Category> categoryList;
    List<Item> itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setCategoriesAdapter();
        setItemsAdapter();
    }

    private void setCategoriesAdapter(){
        categoryList = new ArrayList<>();

        categoryList.add(new Category("Burger", R.drawable.burgers));
        categoryList.add(new Category("Pizza", R.drawable.pizzas));
        categoryList.add(new Category("Sandwich", R.drawable.sandwiches));
        categoryList.add(new Category("Drinks", R.drawable.drinks));
        categoriesAdapter = new CategoriesAdapter(this, categoryList, new CategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, categoryList.get(position).getCategoryName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.categoriesRecyclerView.setAdapter(categoriesAdapter);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        categoriesAdapter = new CategoriesAdapter(this, categoryList, new CategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, categoryList.get(position).getCategoryName(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.categoriesRecyclerView.setAdapter(categoriesAdapter);
        binding.categoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
    }

    private void setItemsAdapter(){
        itemList = new ArrayList<>();

        itemList.add(new Item("Chicken Burger", 250.00f, R.drawable.burger_1_chicken));
        itemList.add(new Item("Cheese Burger", 250.00f, R.drawable.burger_3_cheeze));
        itemList.add(new Item("Grill Burger", 250.00f, R.drawable.burger_6_grill));
        itemList.add(new Item("Chicken Burger", 250.00f, R.drawable.burger_2_chicken));
        itemList.add(new Item("Cheese Burger", 250.00f, R.drawable.burger_4_cheeze));
        itemList.add(new Item("Grill Burger", 250.00f, R.drawable.burger_7_grill));
        itemList.add(new Item("Cheese Burger", 250.00f, R.drawable.burger_5_cheeze));

        itemsAdapter = new ItemsAdapter(this, itemList, new ItemsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                Toast.makeText(MainActivity.this, itemList.get(position).getItemname(), Toast.LENGTH_SHORT).show();
            }
        });

        binding.itemsRecyclerView.setAdapter(itemsAdapter);
        binding.itemsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}