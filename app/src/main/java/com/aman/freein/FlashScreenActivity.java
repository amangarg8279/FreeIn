package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class FlashScreenActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    ImageView logo;
    TextView logo_detail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        logo = findViewById(R.id.logo);
        logo_detail=findViewById(R.id.logo_detail);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onStart() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        AnimatedVectorDrawable drawable = (AnimatedVectorDrawable) logo.getDrawable();

        drawable.start();
        super.onStart();
        YoYo.with(Techniques.BounceInDown).duration(1000).repeat(0).playOn(logo_detail);
        if (firebaseUser != null) {

            DocumentReference documentReference = firebaseFirestore.collection("users").document(firebaseUser.getPhoneNumber());

            documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Intent i = new Intent(FlashScreenActivity.this, HomePageActivity.class);
                        i.putExtra("MobileNumber", firebaseUser.getPhoneNumber());
                        onBackPressed();
                        Toast.makeText(FlashScreenActivity.this, "Hello", Toast.LENGTH_SHORT).show();
                        startActivity(i);
                    } else {
                        Intent i = new Intent(FlashScreenActivity.this, PhoneNumberActivity.class);
                        onBackPressed();
                        startActivity(i);
                        firebaseUser.delete();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Intent i = new Intent(FlashScreenActivity.this, PhoneNumberActivity.class);
                    onBackPressed();
                    startActivity(i);
                    firebaseUser.delete();
                }
            });
        }
        else {
            Intent i = new Intent(FlashScreenActivity.this, PhoneNumberActivity.class);
            onBackPressed();
            startActivity(i);
        }

        super.onStart();
    }
}