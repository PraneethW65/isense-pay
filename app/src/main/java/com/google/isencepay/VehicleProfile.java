package com.google.isencepay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class VehicleProfile extends AppCompatActivity {

    private String reg;
    private FirebaseFirestore db;
    public String TAG="SRA";
    public TextView li;
    public TextView nic;
    public TextView na;
    public TextView carname;
    public TextView regis;
    public TextView address;
    public TextView color;
    public TextView model;
    public TextView type;
    public TextView year;
    public TextView feee;
    public TextView from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_profile);

        nic = findViewById(R.id.nic);
        na = findViewById(R.id.name);
        carname = findViewById(R.id.textView6);
        address = findViewById(R.id.address);
        regis = findViewById(R.id.surname);
        color = findViewById(R.id.color);
        model = findViewById(R.id.model);
        type = findViewById(R.id.type);
        year = findViewById(R.id.year);
        feee = findViewById(R.id.fee);
        from = findViewById(R.id.from);

        reg = getIntent().getStringExtra("Vehicle");

        db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Register").document(reg);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        nic.setText("NIC : "+document.get("NIC").toString());
                        color.setText("COLOR : "+document.get("Color").toString());
                        carname.setText(document.get("Model").toString());
                        model.setText("MODEL : "+document.get("Make").toString());
                        type.setText("TYPE : "+document.get("Type").toString());
                        year.setText("YEAR : "+document.get("Year").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        DocumentReference docRef2 = db.collection("license").document(reg);
        docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        na.setText("NAME : "+document.get("oName").toString());
                        address.setText("ADDRESS : "+document.get("address").toString());
                        feee.setText("ANNUAL FEE : "+document.get("AnnualFee").toString());
                        from.setText("VALID FROM : "+document.get("From").toString());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }
}