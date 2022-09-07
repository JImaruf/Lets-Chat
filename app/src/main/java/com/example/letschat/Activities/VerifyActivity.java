package com.example.letschat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.R;
import com.google.firebase.auth.FirebaseAuth;

public class VerifyActivity extends AppCompatActivity {
    String phoneNumber;
    EditText phnET;
    FirebaseAuth auth;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);
        phnET = findViewById(R.id.phoneET);
        auth =FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Sending OTP...");
        if(auth.getCurrentUser()!=null){
            Intent intent = new Intent(VerifyActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        }

    }

    public void continuebtn(View view) {
        dialog.show();
        phoneNumber=phnET.getText().toString();

        Intent intent = new Intent(VerifyActivity.this, OTPActivity.class);
        intent.putExtra("phone",phoneNumber);
        startActivity(intent);
        dialog.dismiss();
    }
}