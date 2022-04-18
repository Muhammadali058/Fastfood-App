package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fastfood_app.Adapters.CategoriesAdapter;
import com.example.fastfood_app.Adapters.ItemsAdapter;
import com.example.fastfood_app.HP;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.Models.User;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityMainBinding;
import com.example.fastfood_app.databinding.NavigationDrawerHeaderBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    NavigationDrawerHeaderBinding headerBinding;
    FirebaseFirestore firestore;
    FirebaseAuth auth;
    CategoriesAdapter categoriesAdapter;
    ItemsAdapter itemsAdapter;
    List<Category> categoryList;
    List<Item> itemList;
    Category selectedCategory = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View headerView = binding.navigationView.getHeaderView(0);
        headerBinding = NavigationDrawerHeaderBinding.bind(headerView);
        setContentView(binding.getRoot());

        checkPermissions();

        init();
        setCategoriesAdapter();
        setItemsAdapter();
        setUserInfo();
        setNavigationDrawer();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        binding.searchTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchItems();
            }
        });

        binding.searchTB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    searchItems();
                    return true;
                }
                return false;
            }
        });

        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.backBtn.setVisibility(View.GONE);
                binding.categoriesLayout.setVisibility(View.VISIBLE);
                binding.searchTB.setText("");

                loadItems(selectedCategory.getId());
            }
        });

        binding.cartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });

        headerBinding.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(auth.getCurrentUser() == null){
                    Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private void setCategoriesAdapter(){
        categoryList = new ArrayList<>();

        categoriesAdapter = new CategoriesAdapter(this, categoryList, new CategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                selectedCategory = categoryList.get(position);
                loadItems(selectedCategory.getId());
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

                selectedCategory = categoryList.get(0);
                loadItems(selectedCategory.getId());
            }
        });
    }

    private void setItemsAdapter(){
        itemList = new ArrayList<>();
        itemsAdapter = new ItemsAdapter(this, itemList);
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

    private void setUserInfo(){
        if(auth.getCurrentUser() != null) {
            firestore.collection("users").document(auth.getUid())
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    HP.user = documentSnapshot.toObject(User.class);

                    headerBinding.username.setText(HP.user.getUsername());
                    Glide.with(MainActivity.this).load(HP.user.getImageUrl())
                            .placeholder(R.drawable.avatar)
                            .into(headerBinding.image);


                    if(HP.user.getUserType() == 1)
                        binding.navigationView.inflateMenu(R.menu.navigation_menu_admin);
                    else
                        binding.navigationView.inflateMenu(R.menu.navigation_menu);

                }
            });
        }
        else
            binding.navigationView.inflateMenu(R.menu.navigation_menu);
    }

    private void setNavigationDrawer(){
        setSupportActionBar(binding.toolbar);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,R.string.open,R.string.close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);;
        actionBarDrawerToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.adminPanel:
                        startAdminPanel();
                        break;
                    case R.id.orders:
                        if(auth.getCurrentUser() != null) {
                            Intent intent = new Intent(MainActivity.this, OrdersActivity.class);
                            startActivity(intent);
                        }else{
                            Intent intent = new Intent(MainActivity.this, PhoneNumberActivity.class);
                            startActivity(intent);
                        }
                        closeDrawer();
                        break;
                    case R.id.exit:
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    private void closeDrawer(){
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    private void startAdminPanel(){
        Intent intent = new Intent(MainActivity.this, AdminMainActivity.class);
        startActivity(intent);
        closeDrawer();
    }

    private void checkPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Please allow work properly.", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == 2){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Please allow work properly.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}