package com.google.isencepay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class NewVehicle extends AppCompatActivity {

    public String TAG="SRA";
    private LinearLayout LL2;
    private String nic;
    public FirebaseFirestore db;
    public Map<String, String> Nuser;
    public EditText customReg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_vehicle);

        LL2=(LinearLayout) this.findViewById(R.id.ll2);
        Nuser= new HashMap<>();
        customReg=findViewById(R.id.editTextTextPersonName);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        nic=sharePref.getString("NIC",null);
        Log.d(TAG, "tag worked" +nic);

        db = FirebaseFirestore.getInstance();

        db.collection("MyList")
                .whereEqualTo("NIC", nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                Nuser.put(document.get("Vehicle").toString(),document.get("NIC").toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Register")
                .whereEqualTo("NIC", nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                TextView tv=new TextView(NewVehicle.this);
                                lparams.gravity = Gravity.CENTER;
                                lparams.setMargins(10, 40, 10, 10);
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vehicle,0,0,0);
                                tv.setTextSize(20);
                                tv.setLayoutParams(lparams);
                                tv.setBackgroundColor(Color.BLUE);
                                String value = Nuser.get(document.getId());
                                if (value == null) {
                                    tv.setText(document.get("Model").toString()+" : Click to Add");

                                    tv.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            addVehicle(document.getId());
                                        }
                                    });
                                } else {
                                    tv.setText(document.get("Model").toString()+" : Already Added");
                                    LL2.addView(tv);
                                }

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    public void addVehicle(String reg){

        Map<String, String> user = new HashMap<>();
        user.put("NIC", nic);
        user.put("Vehicle", reg);

        // Add a new document with a generated ID
        db.collection("MyList")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(NewVehicle.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(NewVehicle.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void addCustomVehicle(View view){


        String Reg=customReg.getText().toString();
        Map<String, String> user = new HashMap<>();
        user.put("NIC", nic);
        user.put("Vehicle", Reg);

        // Add a new document with a generated ID
        db.collection("MyList")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        Toast.makeText(NewVehicle.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(NewVehicle.this, Vehicles.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                        Toast.makeText(NewVehicle.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}