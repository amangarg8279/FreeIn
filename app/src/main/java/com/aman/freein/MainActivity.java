package com.aman.freein;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    LinearProgressIndicator LinearProgressIndicator_Main;
    String phoneNumber;

    TextInputEditText Name, ContactNumber, Email, State, City, PromoCode;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearProgressIndicator_Main = findViewById(R.id.data_save_progress_bar);
        Name=findViewById(R.id.fullname);
        ContactNumber=findViewById(R.id.contactNumber);
        Email=findViewById(R.id.Email);
        State=findViewById(R.id.state);
        City=findViewById(R.id.city);
        PromoCode=findViewById(R.id.Promo_Code);
        phoneNumber = getIntent().getStringExtra("MobileNumber");
        ContactNumber.setText(phoneNumber);
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearProgressIndicator_Main.setVisibility(View.VISIBLE);

                //                Intent i = new Intent(MainActivity.this, PhoneNumberActivity.class);
//
//                startActivity(i);
            }
        });


    }
}