package com.example.letschat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.letschat.databinding.ActivityOtpactivityBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import in.aabhasjindal.otptextview.OTPListener;

public class OTPActivity extends AppCompatActivity {
    String verificationID;
    ActivityOtpactivityBinding binding;
    FirebaseAuth mAuth;
    String phoneNumber;
    String otp;
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpactivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth=FirebaseAuth.getInstance();
        phoneNumber = getIntent().getStringExtra("phone");
       binding.textView.setText("Verify "+phoneNumber);

       PhoneAuthOptions phoneAuthOptions = PhoneAuthOptions.newBuilder(mAuth)
                       .setPhoneNumber(phoneNumber)
                               .setTimeout(60L,TimeUnit.SECONDS)
                                       .setActivity(OTPActivity.this)
                                               .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                                   @Override
                                                   public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                                                   }

                                                   @Override
                                                   public void onVerificationFailed(@NonNull FirebaseException e) {

                                                   }

                                                   @Override
                                                   public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                                       super.onCodeSent(s, forceResendingToken);
                                                       verificationID = s;
                                                   }
                                               }).build();

       PhoneAuthProvider.verifyPhoneNumber(phoneAuthOptions);



      binding.otppin.setOtpListener(new OTPListener() {
          @Override
          public void onInteractionListener() {

          }

          @Override
          public void onOTPComplete(String otp) {
              dialog.show();
              PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID,otp);

              mAuth.signInWithCredential(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                  @Override
                  public void onComplete(@NonNull Task<AuthResult> task) {
                      if(task.isSuccessful())
                      {
                          dialog.dismiss();
                          Toast.makeText(OTPActivity.this, "Success", Toast.LENGTH_SHORT).show();
                          startActivity(new Intent(OTPActivity.this, SetupProfileActivity.class));
                      }
                      else
                      {
                          Toast.makeText(OTPActivity.this, "sorry", Toast.LENGTH_SHORT).show();
                      }
                  }
              });

          }
      });



    }
}