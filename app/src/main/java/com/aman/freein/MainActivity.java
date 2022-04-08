package com.aman.freein;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    LinearProgressIndicator LinearProgressIndicator_Main;
    private CoordinatorLayout Main_CoordinatorLayout;
    String phoneNumber;
    TextInputLayout name_TextInputLayout, email_TextInputLayout, state_TextInputLayout, city_TextInputLayout, Promo_Code_layout;
    TextInputEditText Name, ContactNumber, Email, State, City, PromoCode;
    FirebaseFirestore firebaseFirestore;
    Long Reward, RewardCount, PaymentReward, PaymentRewardCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearProgressIndicator_Main = findViewById(R.id.data_save_progress_bar);
        Name = findViewById(R.id.fullname);
        ContactNumber = findViewById(R.id.contactNumber);
        Email = findViewById(R.id.Email);
        State = findViewById(R.id.state);
        Reward = Long.valueOf(30);
        RewardCount = Long.valueOf(0);
        PaymentReward = Long.valueOf(0);
        PaymentRewardCount = Long.valueOf(0);
        City = findViewById(R.id.city);
        PromoCode = findViewById(R.id.Promo_Code);
        firebaseFirestore = FirebaseFirestore.getInstance();
        String name = Name.getText().toString().trim();
        name_TextInputLayout = findViewById(R.id.fullName_layout);
        email_TextInputLayout = findViewById(R.id.Email_layout);
        state_TextInputLayout = findViewById(R.id.State_layout);
        city_TextInputLayout = findViewById(R.id.City_layout);
        Main_CoordinatorLayout = findViewById(R.id.Main_CoordinatorLayout);
        Promo_Code_layout = findViewById(R.id.Promo_Code_layout);
        phoneNumber = getIntent().getStringExtra("MobileNumber");
        ContactNumber.setText(phoneNumber);

        findViewById(R.id.button_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Name.getText().toString().isEmpty() || Name.getText().toString().trim().length() < 4
                        || Email.getText().toString().isEmpty() || Email.getText().toString().trim().length() < 4
                        || State.getText().toString().isEmpty() || State.getText().toString().trim().length() < 4
                        || City.getText().toString().isEmpty() || City.getText().toString().trim().length() < 4
                ) {
                    Snackbar.make(Main_CoordinatorLayout, "Please check entered fields, length must be more then 4 ", Snackbar.LENGTH_SHORT).show();
                } else {
                    if (!PromoCode.getText().toString().trim().isEmpty()) {
                        firebaseFirestore.collection("PromoCodes").document(PromoCode.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.getResult().exists()) {
                                    Toast.makeText(MainActivity.this, "Promo Code Applied", Toast.LENGTH_SHORT).show();
                                    GenreateReward(PromoCode.getText().toString().trim());
                                    createUserandPromoDatabase();
                                } else {
                                    Toast.makeText(MainActivity.this, "Cannot determine the promocode", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        createUserandPromoDatabase();
                    }

                }
            }
        });
    }

    private void GenreateReward(String PromoCode) {


        firebaseFirestore.collection("PromoCodes").document(PromoCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String user_promoPhoneNumber = documentSnapshot.getString("Contact Number");
                    DocumentReference documentReference_reward = firebaseFirestore.collection("users").document(user_promoPhoneNumber);
                    DocumentReference documentReference_rewardShareData = firebaseFirestore.collection("RewardShareData").document(user_promoPhoneNumber);
                    Map<String, Object> userReward = new HashMap<>();
                    documentReference_reward.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Long rewardCount = documentSnapshot.getLong("RewardCount");
                            if (rewardCount < 3) {
                                userReward.put("RewardPoint", documentSnapshot.getLong("RewardPoint") + 12);
                                userReward.put("RewardCount", documentSnapshot.getLong("RewardCount") + 1);
                                updateRewardShareData(documentReference_rewardShareData);
                            } else {
                                userReward.put("RewardPoint", documentSnapshot.getLong("RewardPoint") + 5);
                                userReward.put("RewardCount", documentSnapshot.getLong("RewardCount") + 1);
                                updateRewardShareData(documentReference_rewardShareData);
                            }

                            documentReference_reward.update(userReward).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(MainActivity.this, "Reward updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, "cant Reward updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });


                }
            }
        });
    }

    private void updateRewardShareData(DocumentReference documentReference_rewardShareData) {
documentReference_rewardShareData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        Map<String, Object> userRewardShare = new HashMap<>();
        if(task.getResult().exists()){
            documentReference_rewardShareData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Long PersonCount = documentSnapshot.getLong("PersonCount");
                    userRewardShare.put("PersonCount", documentSnapshot.getLong("PersonCount")+Long.valueOf(1));
                    userRewardShare.put("PersonNameCount"+(PersonCount+1),Name.getText().toString().trim());
                    userRewardShare.put("PersonMobileCount"+(PersonCount+1), "+91"+phoneNumber);
                    documentReference_rewardShareData.update(userRewardShare).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(MainActivity.this, "RewardStore updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "cant RewardStore updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
        else
        {
            userRewardShare.put("PersonCount", 1);
            userRewardShare.put("PersonNameCount"+1,Name.getText().toString().trim());
            userRewardShare.put("PersonMobileCount"+1, "+91"+phoneNumber );
            documentReference_rewardShareData.set(userRewardShare).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(MainActivity.this, "RewardStore updated", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "cant RewardStore updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }
    }
});

    }

    private void createUserandPromoDatabase() {
        name_TextInputLayout.setEnabled(false);
        email_TextInputLayout.setEnabled(false);
        state_TextInputLayout.setEnabled(false);
        city_TextInputLayout.setEnabled(false);

        LinearProgressIndicator_Main.setVisibility(View.VISIBLE);
        String document_Number = "+91" + phoneNumber;
        String myPromoCode = generate_PromoCode(Name.getText().toString().trim());
        createPromoData(myPromoCode);
        DocumentReference documentReference = firebaseFirestore.collection("users").document(document_Number);
        Map<String, Object> user = new HashMap<>();
        user.put("FullName", Name.getText().toString().trim());
        user.put("Contact Number", phoneNumber);
        user.put("Email", Email.getText().toString().trim());
        user.put("State", State.getText().toString().trim());
        user.put("City", City.getText().toString().trim());
        user.put("PromoCode", PromoCode.getText().toString().trim());
        user.put("MyPromoCode", myPromoCode.trim());
        user.put("PaymentReward", PaymentReward);
        user.put("PaymentRewardCount", PaymentRewardCount);
        user.put("PaymentStatus", "Pending");
        user.put("RewardPoint", Reward);
        user.put("RewardCount", RewardCount);
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
    }


    private void createPromoData(String myPromoCode) {

        DocumentReference documentReference = firebaseFirestore.collection("PromoCodes").document(myPromoCode);
        Map<String, Object> user = new HashMap<>();
        user.put("FullName", Name.getText().toString().trim());
        user.put("Contact Number", "+91" + phoneNumber);
        user.put("Email", Email.getText().toString().trim());
        user.put("PromoCode", PromoCode.getText().toString().trim().toUpperCase(Locale.ROOT));
        user.put("MyPromoCode", myPromoCode.trim());
        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                LinearProgressIndicator_Main.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Promocode genreated and stored", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, "we can't genreated Promocode" + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String generate_PromoCode(String name) {

        int min = 1;
        int max = 900;
        name = name.replaceAll("\\s", "");
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 4) { // length of the random string.
            int index = (int) (rnd.nextFloat() * name.length());
            salt.append(name.charAt(index));
        }
        String saltStr = salt.toString();
        int b = (int) (Math.random() * (max - min + 1) + min);
        String promoCode = saltStr.toUpperCase(Locale.ROOT) + b;
        check_existing_promoCode(promoCode, name);
        return promoCode;
    }

    private void check_existing_promoCode(String code, String name) {
        firebaseFirestore.collection("PromoCodes").document(code).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.getResult().exists()) {
                    generate_PromoCode(name);
                }
            }
        });
    }

}