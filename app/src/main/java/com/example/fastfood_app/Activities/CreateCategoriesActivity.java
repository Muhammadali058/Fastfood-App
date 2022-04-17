package com.example.fastfood_app.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastfood_app.Adapters.SearchCategoriesAdapter;
import com.example.fastfood_app.Models.Category;
import com.example.fastfood_app.Models.Item;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityCreateCategoriesBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateCategoriesActivity extends AppCompatActivity {

    ActivityCreateCategoriesBinding binding;
    FirebaseFirestore firestore;
    SearchCategoriesAdapter searchCategoriesAdapter;
    List<Category> list;
    String imageUrl = null;
    Category selectedCategory = null;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateCategoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        loadData();
    }

    private void init(){
        firestore = FirebaseFirestore.getInstance();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");

        list = new ArrayList<>();
        searchCategoriesAdapter = new SearchCategoriesAdapter(this, list, new SearchCategoriesAdapter.OnClickListener() {
            @Override
            public void onClick(int position) {
                selectedCategory = list.get(position);
                setData();
            }
        }, new SearchCategoriesAdapter.OnLongClickListener() {
            @Override
            public void onClick(int position) {
                deleteData(list.get(position).getId());
            }
        });

        binding.recyclerView.setAdapter(searchCategoriesAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CreateCategoriesActivity.this, ImageViewActivity.class);
                if(selectedCategory != null)
                    intent.putExtra("imageUrl", selectedCategory.getImageUrl());
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

    }

    private void searchData() {
        String text = binding.searchTB.getText().toString();

        firestore.collection("categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {
                list.clear();

                for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                    Category category = queryDocumentSnapshot.toObject(Category.class);
                    category.setId(queryDocumentSnapshot.getId());

                    if(category.getCategoryName().toLowerCase().contains(text.toLowerCase()))
                        list.add(category);
                }

                searchCategoriesAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadData(){
        if(binding.searchTB.getText().length() > 0){
            searchData();
        }else {
            firestore.collection("categories").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot querySnapshot) {
                    list.clear();

                    for (QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot) {
                        Category category = queryDocumentSnapshot.toObject(Category.class);
                        category.setId(queryDocumentSnapshot.getId());
                        list.add(category);
                    }

                    searchCategoriesAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void insertOrUpdateData(){
        if(binding.categoryNameTB.getText().length() > 0) {
            if(selectedCategory == null) {
                insertData();
            }
            else {
                updateData(selectedCategory.getId());
            }
        }else {
            Toast.makeText(this, "Enter Category Name", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertData(){
        DocumentReference documentReference = firestore.collection("categories").document();
        Category category = getData();
        category.setId(documentReference.getId());

        documentReference.set(category).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            clearData();
                            loadData();
                        }
                    }
                });
    }

    private void updateData(String id){
        Category category = getData();
        category.setId(selectedCategory.getId());

        firestore.collection("categories").document(id)
                .set(category, SetOptions.merge()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    clearData();
                    loadData();
                }
            }
        });
    }

    private void deleteData(String id){
        firestore.collection("categories").document(id).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                loadData();
            }
        });
    }

    private Category getData(){
        Category category = new Category();
        category.setCategoryName(binding.categoryNameTB.getText().toString());
        category.setImageUrl(imageUrl);

        return category;
    }

    private void setData(){
        binding.categoryNameTB.setText(selectedCategory.getCategoryName());
        imageUrl = selectedCategory.getImageUrl();
    }

    private void clearData(){
        selectedCategory = null;
        imageUrl = null;

        binding.categoryNameTB.setText("");

        binding.categoryNameTB.requestFocus();
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