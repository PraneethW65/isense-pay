package com.google.isencepay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Home extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private TextView uname;
    private ImageView userPic;
    private FirebaseAuth mAuth;
    private String proPic;
    private String name;
    private String uid;
    private String nic;

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

        mAuth.signOut();
    }

}