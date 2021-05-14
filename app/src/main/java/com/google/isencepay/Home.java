package com.google.isencepay;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class Home extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private TextView uname;
    private ImageView userPic;
    private FirebaseAuth mAuth;
    private String proPic;
    private String name;
    private String uid;
    private String nic;
    public String TAG = "SRA";
    private FirebaseFirestore db;
    private String id;
    public String regNo;
    public String monthsData;
    public String CHANNEL_ID = "chid";
    public int notificationId=1234521234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        nic=sharePref.getString("NIC",null);

        if(nic == null){
            Intent intent=new Intent(this, First.class);
            startActivity(intent);
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        uname=findViewById(R.id.name);
        userPic=(ImageView)findViewById(R.id.upic);



        mAuth= FirebaseAuth.getInstance();
        FirebaseUser user=mAuth.getCurrentUser();
        assert user != null;
        proPic = user.getPhotoUrl().toString();
        name=user.getDisplayName();
        uid=user.getUid();

        uname.setText(name);

        Glide.with(this).load(proPic).into(userPic);
        Glide.with(getApplicationContext()).load(proPic)
                .thumbnail(0.5f)
                .crossFade()
                .into(userPic);

        userPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goProfile();
            }
        });

        checkNotifications();
        checkExpiredate();
    }

    public void goProfile(){
        Intent intent=new Intent(this, Profile.class);
        startActivity(intent);
    }

    public void goVehicle(View view){
        Intent intent=new Intent(this, Vehicles.class);
        startActivity(intent);
    }

    public void goOffence(View view){
        Intent intent=new Intent(this, Offences.class);
        startActivity(intent);
    }

    public void goReminder(View view){
        Intent intent=new Intent(this, Reminders.class);
        startActivity(intent);
    }

    public void goHistor(View view){
        Intent intent=new Intent(this, History.class);
        startActivity(intent);
    }

    public void signout(View view){

        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("NIC",null);
        editor.apply();

        Intent intent=new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();

        mAuth.signOut();
    }

    public void checkNotifications(){

        db = FirebaseFirestore.getInstance();
        db.collection("Reminders")
                .whereEqualTo("NIC", nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private int notificationId=123456;

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                int months=Integer.parseInt(document.get("Months").toString());

                                DocumentReference docRef = db.collection("licence").document(document.get("Vehicle").toString());
                                String vehicle=document.get("Vehicle").toString();
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                Timestamp timestamp=(Timestamp)document.get("From");
                                                Date expireDate = timestamp.toDate();
                                                String car=vehicle;


                                                Calendar c = Calendar.getInstance();
                                                c.setTime(expireDate);
                                                c.add(Calendar.MONTH, -(months));
                                                Date today = new Date();
                                                if(today == c.getTime()){
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, CHANNEL_ID)
                                                            .setSmallIcon(R.drawable.car)
                                                            .setContentTitle(car)
                                                            .setContentText("Reminder for Expire Date "+expireDate.toString())
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        CharSequence name = "MyNotifi";
                                                        String description = "MyNotifi";
                                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                        channel.setDescription(description);
                                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);
                                                    notificationManager.notify(notificationId, builder.build());
                                                }



                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });


                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void checkExpiredate(){

        db = FirebaseFirestore.getInstance();
        db.collection("MyList")
                .whereEqualTo("NIC", nic)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String vehicle=document.get("Vehicle").toString();
                                DocumentReference docRef = db.collection("license").document(document.get("Vehicle").toString());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                Timestamp timestamp=(Timestamp)document.get("From");
                                                Date expireDate = timestamp.toDate();
                                                String car=vehicle;

                                                Date today = new Date();
                                                if(today == expireDate){
                                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(Home.this, CHANNEL_ID)
                                                            .setSmallIcon(R.drawable.car)
                                                            .setContentTitle(car)
                                                            .setContentText("Today is the finale day renew your licence")
                                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        CharSequence name = "MyNotifi";
                                                        String description = "MyNotifi";
                                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                                                        channel.setDescription(description);
                                                        NotificationManager notificationManager = getSystemService(NotificationManager.class);
                                                        notificationManager.createNotificationChannel(channel);
                                                    }

                                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Home.this);
                                                    notificationManager.notify(notificationId, builder.build());
                                                }
                                            } else {
                                                Log.d(TAG, "No such document");
                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                        }
                                    }
                                });

                            }

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });



    }

}