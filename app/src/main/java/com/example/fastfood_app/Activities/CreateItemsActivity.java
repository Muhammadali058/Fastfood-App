package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.SearchItemsAdapter;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.databinding.ActivityCreateItemsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.List;

public class CreateItemsActivity extends AppCompatActivity {

    ActivityCreateItemsBinding binding;
    FirebaseFirestore firestore;
    SearchItemsAdapter searchItemsAdapter;
    List<Item> list;
    String imageUrl = null;
    Item selectedItem = null;
    Category selectedCategory = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateItemsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        loadData();
        loadCategories();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        list = new ArrayList<>();
        searchItemsAdapter = new SearchItemsAdapter(this, list, new SearchItemsAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                selectedItem = list.get(position);
                setData();
            }
        }, new SearchItemsAdapter.OnLongClickListener() {
            @Override
            public void onClick(int position) {
                deleteData(list.get(position));
            }
        });

        binding.recyclerView.setAdapter(searchItemsAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateItemsActivity.this, ImageViewActivity.class);
                if(selectedItem != null)
                    intent.putExtra("imageUrl", selectedItem.getImageUrl());
                else
                    intent.putExtra("imageUrl", imageUrl);
                startActivityForResult(intent, 123);
            }
        });

        binding.cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearData();
            }
        });

        binding.saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertOrUpdateData();
            }
        });

        // Search
        binding.searchTbLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        binding.searchTB.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_ACTION_SEARCH){
                    loadData();
                    return true;
                }
                return false;
            }
        });

        // Categories
        binding.categoryTBLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.categoryTB.showDropDown();
            }
        });

        binding.categoryTB.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b){
                    binding.categoryTB.showDropDown();
                }
            }
        });

        binding.categoryTB.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = (Category) adapterView.getItemAtPosition(i);
            }
        });

    }

    private void searchData() {
        String text = binding.searchTB.getText().toString();

        firestore.collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                list.clear();

                for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                    Item item = queryDocumentSnapshot.toObject(Item.class);
                    item.setId(queryDocumentSnapshot.getId());

                    if(item.getItemname().toLowerCase().contains(text.toLowerCase()))
                        list.add(item);
                }

                searchItemsAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadCategories(){
        firestore.collection("categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                List<Category> categoryList = new ArrayList<>();

                for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                    Category category = queryDocumentSnapshot.toObject(Category.class);
                    categoryList.add(category);
                }

                ArrayAdapter<Category> arrayAdapter = new ArrayAdapter<>(CreateItemsActivity.this, android.R.layout.simple_list_item_1, categoryList);
                binding.categoryTB.setAdapter(arrayAdapter);
            }
        });
    }

    private void loadData(){
        if(binding.searchTB.getText().length() > 0){
            searchData();
        }else {
            firestore.collection("items").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    list.clear();

                    for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                        Item item = queryDocumentSnapshot.toObject(Item.class);
                        item.setId(queryDocumentSnapshot.getId());
                        list.add(item);
                    }

                    searchItemsAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void insertOrUpdateData(){
        if(binding.itemnameTB.getText().length() > 0) {
            if(selectedItem == null) {
                insertData();
            }
            else {
                updateData(selectedItem);
            }
        }else {
            Toast.makeText(this, "Enter Itemname", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertData(){
        DocumentReference documentReference = firestore.collection("items").document();
        Item item = getData();
        item.setId(documentReference.getId());

        documentReference.set(item).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    clearData();
                    loadData();
                }
            }
        });
    }

    private void updateData(Item item){
        firestore.collection("items").document(item.getId())
                .set(getData(), SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    clearData();
                    loadData();
                }
            }
        });
    }

    private void deleteData(Item item){
        firestore.collection("items").document(item.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loadData();
            }
        });
    }

    private Item getData(){
        Item item = new Item();
        item.setItemname(binding.itemnameTB.getText().toString());
        item.setPrice(Float.valueOf(binding.priceTB.getText().toString()));
        item.setDescription(binding.descriptionTB.getText().toString());
        item.setImageUrl(imageUrl);

        if(selectedCategory != null)
            item.setCategoryId(selectedCategory.getId());
        else
            item.setCategoryId(null);

        return item;
    }

    private void setData(){
        binding.itemnameTB.setText(selectedItem.getItemname());
        binding.priceTB.setText(String.valueOf(selectedItem.getPrice()));
        binding.descriptionTB.setText(selectedItem.getDescription());
        imageUrl = selectedItem.getImageUrl();

        if(selectedItem.getCategoryId() != null){
            getCategory(selectedItem.getCategoryId());
        }
    }

    private void clearData(){
        selectedItem = null;
        selectedCategory = null;
        imageUrl = null;

        binding.itemnameTB.setText("");
        binding.priceTB.setText("");
        binding.descriptionTB.setText("");
        binding.categoryTB.setText("");

        binding.itemnameTB.requestFocus();
    }

    private void getCategory(String id){
        firestore.collection("categories").whereEqualTo("id", id).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                    selectedCategory = (Category) queryDocumentSnapshot.toObject(Category.class);
                    binding.categoryTB.setText(selectedCategory.getCategoryName());
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 123 && data != null){
            imageUrl = data.getExtras().getString("imageUrl");
            if(imageUrl != null){
                insertOrUpdateData();
            }
        }
    }

}