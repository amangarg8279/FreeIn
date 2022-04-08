package com.aman.freein;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.razorpay.Checkout;
import com.razorpay.PaymentResultListener;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomePageActivity extends AppCompatActivity implements PaymentResultListener {
    TextView phone_Number,Name;
    FirebaseFirestore firebaseFirestore;
    String fetch_phone;
    TextView Reward_item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        phone_Number = findViewById(R.id.home_number);
        Name=findViewById(R.id.home_name);
        Checkout.preload(getApplicationContext());

        fetch_phone = getIntent().getStringExtra("MobileNumber");
        Reward_item = findViewById(R.id.Reward_item);
        firebaseFirestore = FirebaseFirestore.getInstance();
        fetch_user_data(getIntent().getStringExtra("MobileNumber"));

        findViewById(R.id.wifiPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startPayment();
            }
        });
    }


    private void fetch_user_data(String mobileNumber) {
        DocumentReference documentReference = firebaseFirestore.collection("users").document(fetch_phone.trim());
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long RewardPoint = documentSnapshot.getLong("RewardPoint");
                    Long PaymentReward = documentSnapshot.getLong("PaymentReward");

                    DocumentReference documentReferences = firebaseFirestore.collection("Payments").document(fetch_phone.trim());
                    documentReferences.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if (documentSnapshot.exists()) {
                                Long PaymentPoint = documentSnapshot.getLong("PaymentPoint");
                                String totalsum=String.valueOf((RewardPoint+PaymentReward+PaymentPoint));
                                Reward_item.setText(totalsum);
                            }
                            else {
                                String totalsum=String.valueOf((RewardPoint+PaymentReward));
                                Reward_item.setText(totalsum);
                            }
                        }
                    });

                    phone_Number.setText(documentSnapshot.get("Contact Number").toString());
                    Name.setText(documentSnapshot.get("FullName").toString());




                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
        DocumentReference documentReference_RewardShare = firebaseFirestore.collection("RewardShareData").document(fetch_phone.trim());


        documentReference_RewardShare.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Long n = documentSnapshot.getLong("PersonCount");
                    String[] nameArray = new String[n.intValue()];
                    String[] mobileArray = new String[n.intValue()];
                    String[] PaymentStatusArray = new String[n.intValue()];
                    for (int i = 0; i < n; i++) {
                        nameArray[i] = (String) documentSnapshot.get("PersonNameCount" + (i + 1));
                        mobileArray[i] = (String) documentSnapshot.get("PersonMobileCount" + (i + 1));

                    }

                    ArrayAdapter adapter = new ArrayAdapter<String>(HomePageActivity.this, R.layout.activity_listview, nameArray);
                    ListView listView = (ListView) findViewById(R.id.name_list);
                    listView.setAdapter(adapter);

                    ArrayAdapter adapter2 = new ArrayAdapter<String>(HomePageActivity.this, R.layout.activity_listview, mobileArray);
                    ListView listView2 = (ListView) findViewById(R.id.mobileNumber_list);
                    listView2.setAdapter(adapter2);

                    ArrayAdapter adapter3 = new ArrayAdapter<String>(HomePageActivity.this, R.layout.activity_listview, mobileArray);
                    ListView listView3 = (ListView) findViewById(R.id.payment_status);
                    listView3.setAdapter(adapter3);

                }
            }
        });

    }

    public void startPayment() {

        Checkout checkout = new Checkout();

        checkout.setImage(R.mipmap.ic_launcher);

        final Activity activity = this;

        try {
            JSONObject options = new JSONObject();
            options.put("name", R.string.app_name);
            options.put("description", "Payment for Anything");
            options.put("send_sms_hash", true);
            options.put("allow_rotation", false);

            //You can omit the image option to fetch the image from dashboard
            options.put("currency", "INR");
            options.put("amount", "30000");

            JSONObject preFill = new JSONObject();
            preFill.put("email", "amangarg895864@gmail.com");
            preFill.put("contact", "8279884992");

            options.put("prefill", preFill);

            checkout.open(activity, options);
        } catch (Exception e) {
            Toast.makeText(activity, "Error in payment: " + e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
            e.printStackTrace();

        }

    }

    @Override
    public void onPaymentSuccess(String s) {

        Toast.makeText(this, "Payment Done", Toast.LENGTH_LONG).show();
        createUserPaymentDatabase();
    }

    @Override
    public void onPaymentError(int i, String s) {
        Toast.makeText(this, "Payment not done " + s, Toast.LENGTH_SHORT)
                .show();
    }



    private void createUserPaymentDatabase() {
        String document_Number = "+91" + phone_Number.getText().toString().trim();
        Date c = Calendar.getInstance().getTime();
        String delegate = "hh:mm aaa";
        String date = (String) DateFormat.format(delegate, Calendar.getInstance().getTime());
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c);
        DocumentReference documentReference = firebaseFirestore.collection("Payments").document(document_Number);
        Map<String, Object> userPayment = new HashMap<>();
        userPayment.put("FullName", Name.getText().toString().trim());
        userPayment.put("Contact Number", document_Number);
        userPayment.put("PaymentTime", date);
        userPayment.put("PaymentDate", formattedDate);
        userPayment.put("PaymentPoint", 300);

        documentReference.set(userPayment).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                //getting phone number
                DocumentReference documentReference = firebaseFirestore.collection("users").document(fetch_phone.trim());


                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        //getting parent promocode

                        Map<String, Object> userPaymentStatus = new HashMap<>();
                        userPaymentStatus.put("PaymentStatus", "Done");
                        documentReference.update(userPaymentStatus).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(HomePageActivity.this, "Updaeted Payment Status", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String Parent_PromoCode = (String) documentSnapshot.get("PromoCode");

                        Toast.makeText(HomePageActivity.this, Parent_PromoCode, Toast.LENGTH_SHORT).show();

                        if (!Parent_PromoCode.isEmpty()) {

                            DocumentReference documentReference_parent_Prmomo = firebaseFirestore.collection("PromoCodes").document(Parent_PromoCode);

                            documentReference_parent_Prmomo.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    //parent Number

                                    String Parent_contact_number = (String) documentSnapshot.get("Contact Number");

                                    Toast.makeText(HomePageActivity.this, Parent_contact_number, Toast.LENGTH_SHORT).show();

                                    DocumentReference documentReference_PaymentRewad = firebaseFirestore.collection("users").document(Parent_contact_number);


                                    DocumentReference documentReference_PaymentShareData = firebaseFirestore.collection("PaymentShareData").document(Parent_contact_number);
                                    Map<String, Object> PaymentReward = new HashMap<>();
                                    documentReference_PaymentRewad.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshots) {
                                            //PaymentRewardCount
                                            Toast.makeText(HomePageActivity.this, documentSnapshots.getLong("PaymentRewardCount").toString(), Toast.LENGTH_SHORT).show();
                                            Long PaymentRewardCount = documentSnapshots.getLong("PaymentRewardCount");

                                            if (PaymentRewardCount < 3) {
                                                PaymentReward.put("PaymentReward", documentSnapshots.getLong("PaymentReward") + 100);
                                                PaymentReward.put("PaymentRewardCount", documentSnapshots.getLong("PaymentRewardCount") + 1);
                                                updatePaymentRewardShareData(documentReference_PaymentShareData);
                                            } else {
                                                PaymentReward.put("PaymentReward", documentSnapshots.getLong("PaymentReward") + 25);
                                                PaymentReward.put("PaymentRewardCount", documentSnapshots.getLong("PaymentRewardCount") + 1);
                                                updatePaymentRewardShareData(documentReference_PaymentShareData);
                                            }

                                            documentReference_PaymentRewad.update(PaymentReward).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Toast.makeText(HomePageActivity.this, "PaymentReward updated", Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(HomePageActivity.this, "cant PaymentReward updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    });


                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HomePageActivity.this, "Payment Data not Saved", Toast.LENGTH_SHORT).show();

            }
        });
    }


    private void updatePaymentRewardShareData(DocumentReference documentReference_PaymentShareData) {
        documentReference_PaymentShareData.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                Map<String, Object> userRewardShare = new HashMap<>();
                if(task.getResult().exists()){
                    documentReference_PaymentShareData.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            Long PersonCount = documentSnapshot.getLong("PersonPaymentCount");
                            userRewardShare.put("PersonPaymentCount", documentSnapshot.getLong("PersonPaymentCount")+Long.valueOf(1));
                            userRewardShare.put("PersonPaymentNameCount"+(PersonCount+1),   Name.getText().toString().trim());
                            userRewardShare.put("PersonPaymentMobileCount"+(PersonCount+1), "+91"+phone_Number.getText().toString().trim());
                            documentReference_PaymentShareData.update(userRewardShare).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(HomePageActivity.this, " Payement RewardStore updated", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(HomePageActivity.this, " Payement cant RewardStore updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
                }
                else
                {
                    userRewardShare.put("PersonPaymentCount", 1);
                    userRewardShare.put("PersonPaymentNameCount"+1,Name.getText().toString().trim());
                    userRewardShare.put("PersonPaymentMobileCount"+1, "+91"+phone_Number.getText().toString().trim() );
                    documentReference_PaymentShareData.set(userRewardShare).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(HomePageActivity.this, "Payement RewardStore updated", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(HomePageActivity.this, "Payment cant RewardStore updated" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }
}
