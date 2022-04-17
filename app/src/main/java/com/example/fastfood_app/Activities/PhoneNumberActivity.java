package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.example.fastfood_app.HP;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_number_layout);
        TextView editBtn = dialog.findViewById(R.id.editBtn);
        TextView okBtn = dialog.findViewById(R.id.okBtn);
        TextView phoneNumber = dialog.findViewById(R.id.number);

        binding.phoneNumber.requestFocus();

        String number = "<a href='http://www.google.com'>What's my number?</a>";
        binding.number.setText(HP.removeUnderline(number));

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = binding.code.getText().toString() + binding.phoneNumber.getText().toString();
                phoneNumber.setText(number);
                dialog.show();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                binding.phoneNumber.requestFocus();
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(PhoneNumberActivity.this, VerificationActivity.class);
                String phoneNumber = "+" + binding.code.getText().toString() + binding.phoneNumber.getText().toString();
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                finish();
            }
        });
    }
}