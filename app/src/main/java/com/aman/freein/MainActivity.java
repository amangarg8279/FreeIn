package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    LinearProgressIndicator LinearProgressIndicator_Main;
    String phoneNumber;

    TextInputEditText Name, ContactNumber, Email, State, City, PromoCode;
    Button submit;
    FirebaseFirestore firebaseFirestore;

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
        firebaseFirestore=FirebaseFirestore.getInstance();
        String name=Name.getText().toString().trim();

        phoneNumber = getIntent().getStringExtra("MobileNumber");
        ContactNumber.setText(phoneNumber);
        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LinearProgressIndicator_Main.setVisibility(View.VISIBLE);

                DocumentReference documentReference=firebaseFirestore.collection("users").document(phoneNumber);
                Map<String,Object> user=new HashMap<>();
                user.put("FullName",Name.getText().toString().trim());
                user.put("Contact Number",phoneNumber);

                user.put("Email",Email.getText().toString().trim());
                user.put("State",State.getText().toString().trim());
                user.put("City",City.getText().toString().trim());
                user.put("City",City.getText().toString().trim());
                user.put("PromoCode",PromoCode.getText().toString().trim());
                documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        LinearProgressIndicator_Main.setVisibility(View.INVISIBLE);
                        Intent i = new Intent(MainActivity.this, HomePageActivity.class);
                        i.putExtra("MobileNumber", phoneNumber);
                        onBackPressed();
                        Toast.makeText(MainActivity.this, "Details Saved Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });





//                                Intent i = new Intent(MainActivity.this, PhoneNumberActivity.class);
//
//                startActivity(i);
            }
        });


    }
}