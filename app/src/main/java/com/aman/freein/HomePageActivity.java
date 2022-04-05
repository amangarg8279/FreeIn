package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomePageActivity extends AppCompatActivity {
    TextView phone_Number;
    FirebaseFirestore firebaseFirestore;
    String fetch_phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        phone_Number = findViewById(R.id.home_name);
        fetch_phone = getIntent().getStringExtra("MobileNumber");
        firebaseFirestore = FirebaseFirestore.getInstance();
        fetch_user_data(getIntent().getStringExtra("MobileNumber"));
        findViewById(R.id.wifiPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void fetch_user_data(String mobileNumber) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(fetch_phone.trim());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    phone_Number.setText(documentSnapshot.getString("RewardPoint"));
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }

}