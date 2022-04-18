package com.example.fastfood_app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fastfood_app.HP;
import com.example.fastfood_app.R;
import com.example.fastfood_app.databinding.ActivityPhoneNumberBinding;

public class PhoneNumberActivity extends AppCompatActivity {

    ActivityPhoneNumberBinding binding;
    Dialog dialog;
    String phoneNumber = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhoneNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        dialog = new Dialog(this);
        dialog.setContentView(R.layout.confirm_number_layout);
        TextView editBtn = dialog.findViewById(R.id.editBtn);
        TextView okBtn = dialog.findViewById(R.id.okBtn);
        TextView phoneNumberTV = dialog.findViewById(R.id.number);

        binding.phoneNumber.requestFocus();

        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String number = binding.phoneNumber.getText().toString();

                char[] chars = number.toCharArray();
                if(number.length() == 11){
                    for (int i=0; i < chars.length; i++){
                        if(i == 0)
                            continue;

                        phoneNumber += String.valueOf(chars[i]);
                    }
                }else {
                    phoneNumber = number;
                }

                phoneNumber = "+" + binding.code.getText().toString() + phoneNumber;

                phoneNumberTV.setText(phoneNumber);
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
                intent.putExtra("phoneNumber", phoneNumber);
                startActivity(intent);
                finish();
            }
        });
    }
}