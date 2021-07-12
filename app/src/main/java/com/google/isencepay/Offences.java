package com.google.isencepay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.model.Document;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Offences extends AppCompatActivity {

    private String nic;
    public String TAG="SRA";
    private LinearLayout LL;
    private FirebaseFirestore db;
    private Spinner customReg;
    private EditText months;
    public ArrayAdapter<String> adapter;
    private String reg;
    private String strDate;
    public String add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offences);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        nic=sharePref.getString("NIC",null);
        Log.d(TAG, "tag worked" +nic);

        LL=(LinearLayout) this.findViewById(R.id.ll);

        customReg=findViewById(R.id.reg);
        List<String> vehicles = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, vehicles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customReg.setAdapter(adapter);

        months=findViewById(R.id.months);

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
                                String reg = document.getString("Vehicle");
                                vehicles.add(reg);
                            }

                            adapter.notifyDataSetChanged();

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        db.collection("Report")
                .whereEqualTo("NIC", nic)
                .whereEqualTo("Paid", false)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                TextView tv = new TextView(Offences.this);
                                lparams.gravity = Gravity.CENTER;
                                lparams.setMargins(10, 40, 10, 10);
                                tv.setCompoundDrawablesWithIntrinsicBounds(R.drawable.vehicle, 0, 0, 0);
                                tv.setTextSize(20);
                                tv.setLayoutParams(lparams);
                                tv.setBackgroundColor(Color.GRAY);
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                Timestamp timestamp=(Timestamp)document.get("Date");
                                Date date = timestamp.toDate();
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                strDate = formatter.format(date);
                                reg=document.get("Vehicle").toString();
                                tv.setText(reg+ "     Date : "+strDate+"         Location :"+getAddress(document.getDouble("latitude"),document.getDouble("longitude"))+"      (click to pay)");
                                LL.addView(tv);

                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        pay(document.getId(),reg,strDate);
                                    }
                                });
                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public String getAddress(double lat, double lng) {
        Geocoder geocoder = new Geocoder(Offences.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            if(addresses.isEmpty()){
                add="Location Unknown(Longitute"+lng+",Latitude"+lat+")";
            }else {
                Address obj = addresses.get(0);
                add = obj.getAddressLine(0);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        return add;
    }

    public void pay(String off,String reg,String Date){

        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
        dlgAlert.setMessage("Click OK to confirm payment");
        dlgAlert.setTitle("Payment Confirmation");

        dlgAlert.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Map<String, Object> user = new HashMap<>();
                        user.put("NIC", nic);
                        user.put("Vehicle", reg);
                        user.put("Date", new Timestamp(new Date()));
                        user.put("ReportedDate", Date);
                        user.put("Amount", 2000);

                        // Add a new document with a generated ID
                        db.collection("History")
                                .add(user)
                                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                    @Override
                                    public void onSuccess(DocumentReference documentReference) {
                                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());

                                        DocumentReference DocRef = db.collection("Report").document(off);
                                        DocRef.update("Paid", true)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(Offences.this, "Successfully Paid", Toast.LENGTH_SHORT).show();
                                                        Intent intent=new Intent(Offences.this, Home.class);
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.w(TAG, "Error updating document", e);
                                                    }
                                                });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding document", e);
                                        Toast.makeText(Offences.this, "Something Wrong", Toast.LENGTH_SHORT).show();
                                    }
                                });



                    }
                });

        dlgAlert.setCancelable(true);
        dlgAlert.create().show();



    }
}