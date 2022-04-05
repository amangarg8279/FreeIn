package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class otp_phone_numberActivity extends AppCompatActivity {
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    LinearProgressIndicator LinearProgressIndicator;
    TextView message, Resend_otp, changeNumber;
    Button verify;
    private String phoneNumber, getotp;
    FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_phone_number);
        LinearProgressIndicator = findViewById(R.id.progress_otp);
        message = findViewById(R.id.message);
        changeNumber = findViewById(R.id.changeNumber);
        Resend_otp = findViewById(R.id.resend_otp);

        verify = findViewById(R.id.verify);
        otp1 = findViewById(R.id.otp_box_1);
        otp2 = findViewById(R.id.otp_box_2);
        otp3 = findViewById(R.id.otp_box_3);
        otp4 = findViewById(R.id.otp_box_4);
        otp5 = findViewById(R.id.otp_box_5);
        otp6 = findViewById(R.id.otp_box_6);
        firebaseFirestore = FirebaseFirestore.getInstance();

        phoneNumber = getIntent().getStringExtra("MobileNumber");
        getotp = getIntent().getStringExtra("OTP");

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (otp1.getText().toString().trim().isEmpty() || otp2.getText().toString().trim().isEmpty() || otp3.getText().toString().trim().isEmpty()
                        || otp4.getText().toString().trim().isEmpty() || otp5.getText().toString().trim().isEmpty() || otp6.getText().toString().trim().isEmpty()) {
                    message.setVisibility(View.VISIBLE);
                    message.setText("Please Check OTP");
                    Toast.makeText(otp_phone_numberActivity.this,"Please check OTP bocks",Toast.LENGTH_LONG).show();
                } else {
                    changeNumber.setVisibility(View.INVISIBLE);
                    changeNumber.setEnabled(false);
                    otp1.setEnabled(false);
                    otp2.setEnabled(false);
                    otp3.setEnabled(false);
                    otp4.setEnabled(false);
                    otp5.setEnabled(false);
                    otp6.setEnabled(false);
                    message.setText("Didn't recieve the OTP?");
                    LinearProgressIndicator.setVisibility(View.VISIBLE);
                    verify.setEnabled(false);
                    Resend_otp.setVisibility(View.VISIBLE);
                    message.setVisibility(View.VISIBLE);
                    String user_entered_otp = otp1.getText().toString().trim() + otp2.getText().toString().trim()
                            + otp3.getText().toString().trim() + otp4.getText().toString().trim() + otp5.getText().toString().trim() + otp6.getText().toString().trim();
                    verifyOTP(getotp, user_entered_otp);
                }
            }
        });

        Resend_otp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeNumber.setEnabled(true);
                otp1.setEnabled(true);
                otp2.setEnabled(true);
                otp3.setEnabled(true);
                otp4.setEnabled(true);
                otp5.setEnabled(true);
                otp6.setEnabled(true);
                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                verify.setEnabled(true);
                Resend_otp.setVisibility(View.INVISIBLE);
                message.setVisibility(View.INVISIBLE);
                Resend_otp();
            }
        });
        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                Intent i = new Intent(otp_phone_numberActivity.this, PhoneNumberActivity.class);

                onBackPressed();
                startActivity(i);
            }
        });
    }

    private void Resend_otp() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber("+91" + phoneNumber, 60, TimeUnit.SECONDS, this, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String newbackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                getotp = newbackendOtp;
                Toast.makeText(otp_phone_numberActivity.this, "OTP sended", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                Intent i = new Intent(otp_phone_numberActivity.this, MainActivity.class);
                i.putExtra("MobileNumber", phoneNumber);
                startActivity(i);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                changeNumber.setEnabled(true);
                otp2.setEnabled(true);
                otp3.setEnabled(true);
                otp4.setEnabled(true);
                otp5.setEnabled(true);
                otp6.setEnabled(true);
                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                verify.setEnabled(true);
                Resend_otp.setVisibility(View.INVISIBLE);
                message.setVisibility(View.INVISIBLE);
                Toast.makeText(otp_phone_numberActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void verifyOTP(String getotp, String user_entered_otp) {
        if (getotp != null) {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getotp, user_entered_otp);
            FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                check_user_exist_and_data_save();
                            } else {
                                changeNumber.setVisibility(View.INVISIBLE);
                                otp1.setEnabled(true);
                                otp2.setEnabled(true);
                                otp3.setEnabled(true);
                                otp4.setEnabled(true);
                                otp5.setEnabled(true);
                                otp6.setEnabled(true);
                                LinearProgressIndicator.setVisibility(View.INVISIBLE);
                                verify.setEnabled(true);
                                Resend_otp.setVisibility(View.INVISIBLE);
                                message.setVisibility(View.INVISIBLE);
                                Toast.makeText(otp_phone_numberActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                            }


                        }
                    });

        } else {
            Toast.makeText(this, "Check Internet connection", Toast.LENGTH_LONG).show();
        }


    }

    private void check_user_exist_and_data_save() {

        firebaseFirestore.collection("users").document(phoneNumber).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    LinearProgressIndicator.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(otp_phone_numberActivity.this, HomePageActivity.class);
                    i.putExtra("MobileNumber", phoneNumber);
                    onBackPressed();
                    Toast.makeText(otp_phone_numberActivity.this, "Welcome back", Toast.LENGTH_SHORT).show();
                    startActivity(i);

                } else {
                    LinearProgressIndicator.setVisibility(View.INVISIBLE);
                    Intent i = new Intent(otp_phone_numberActivity.this, MainActivity.class);
                    i.putExtra("MobileNumber", phoneNumber);
                    onBackPressed();
                    startActivity(i);
                }
            }
        });

    }


}