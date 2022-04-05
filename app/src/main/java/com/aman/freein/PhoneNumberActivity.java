package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PhoneNumberActivity extends AppCompatActivity {
    LinearProgressIndicator LinearProgressIndicator;

    TextInputEditText Phone_Number;
    Button Generate_otp;
    TextView try_again;
    FirebaseFirestore firebaseFirestore;
    TextInputLayout PhoneNumberLayout;
    String OTPid, phone_Number;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_number);
        Phone_Number = findViewById(R.id.mobile_number);
        Generate_otp = findViewById(R.id.generate_otp);
        PhoneNumberLayout = findViewById(R.id.phoneNumber_layout);
        phone_Number = Objects.requireNonNull(Phone_Number.getText()).toString().trim();
        firebaseAuth = FirebaseAuth.getInstance();
        LinearProgressIndicator = findViewById(R.id.progress_otp_send);
        try_again = findViewById(R.id.try_again);
        firebaseFirestore = FirebaseFirestore.getInstance();

        findViewById(R.id.generate_otp).setOnClickListener(view -> {
            if (Phone_Number.getText().toString().trim().length() < 10) {
                PhoneNumberLayout.setError("Please check your number");
            } else if (Phone_Number.getText().toString().trim().length() == 10) {
                PhoneNumberLayout.setError(null);
                LinearProgressIndicator.setVisibility(View.VISIBLE);
                Phone_Number.setEnabled(false);
                Generate_otp.setEnabled(false);
                try_again.setVisibility(View.VISIBLE);
                OTpSend(Phone_Number.getText().toString().trim());
            }
        });

        try_again.setOnClickListener(view -> {
            LinearProgressIndicator.setVisibility(View.INVISIBLE);
            Phone_Number.setEnabled(true);
            Generate_otp.setEnabled(true);
            try_again.setVisibility(View.INVISIBLE);
        });

    }

    private void OTpSend(String PhoneNumber) {
        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + PhoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String backendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                OTPid = backendOtp;
                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                Intent i = new Intent(PhoneNumberActivity.this, otp_phone_numberActivity.class);
                i.putExtra("MobileNumber", PhoneNumber);
                i.putExtra("OTP", backendOtp);
                onBackPressed();
                startActivity(i);
                Toast.makeText(PhoneNumberActivity.this, "OTP sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {

            }
        });
    }

}