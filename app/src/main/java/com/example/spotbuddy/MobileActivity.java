package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.concurrent.TimeUnit;

public class MobileActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressDialog otpDialog;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);

        mAuth = FirebaseAuth.getInstance();

        CountryCodePicker country = findViewById(R.id.mobile_box_country);
        EditText phn = findViewById(R.id.mobile_edt_phone);
        Button request = findViewById(R.id.mobile_btn_request);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phn.getText().toString().trim().isEmpty() || phn.getText().toString().length() !=10)  {
                    Toast.makeText(MobileActivity.this, "Invalid Phone Number", Toast.LENGTH_SHORT).show();
                } else {
                    OtpSend();
                }
            }

            private void OtpSend() {
                otpDialog = new ProgressDialog(MobileActivity.this);
                otpDialog.setMessage("Sending OTP");
                otpDialog.setCanceledOnTouchOutside (false);
                otpDialog.setCancelable (false);
                otpDialog.show();
                mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(MobileActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String verificationId,
                                           @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        Intent i = new Intent(MobileActivity.this, OtpActivity.class);
                        i.putExtra("country", "+91");
                        i.putExtra("mobile", phn.getText().toString().trim());
                        i.putExtra("verificationID", verificationId);
                        if (otpDialog.isShowing()) {
                            otpDialog.dismiss();
                        }
                        startActivity(i);
                    }
                };
                PhoneAuthOptions options =
                        PhoneAuthOptions.newBuilder(mAuth)
                                .setPhoneNumber("+91" + phn.getText().toString().trim())       // Phone number to verify
                                .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                                .setActivity(MobileActivity.this)                 // Activity (for callback binding)
                                .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                                .build();
                PhoneAuthProvider.verifyPhoneNumber(options);
            }
        });
    }
}