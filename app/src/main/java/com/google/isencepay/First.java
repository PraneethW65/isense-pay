package com.google.isencepay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.EditText;

public class First extends AppCompatActivity {

    private EditText NIC;
    private String nic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        NIC=findViewById(R.id.nic);


    }

    public void submit(View view){
        nic= NIC.getText().toString();
        SharedPreferences sharePref= PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharePref.edit();
        editor.putString("NIC",nic);
        editor.apply();

        Intent intent=new Intent(this, Home.class);
        startActivity(intent);
        finish();
    }
}