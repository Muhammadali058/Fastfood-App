package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityAddToCartBinding;

public class AddToCartActivity extends AppCompatActivity {

    ActivityAddToCartBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddToCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}