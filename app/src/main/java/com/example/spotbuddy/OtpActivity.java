package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class OtpActivity extends AppCompatActivity {
    private String verificationID;
    ProgressDialog verifyDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);

        edittextInput();

        TextView expire = findViewById(R.id.otp_txt_expire);
        AtomicInteger success = new AtomicInteger();
        new CountDownTimer(60000, 1000) {
            @Override
            public void onTick(long l) {
                expire.setText("Your code will expire in " + (int) l/1000 + "s");
            }

            @Override
            public void onFinish() {
                if (success.get() !=1){
                    Toast.makeText(OtpActivity.this, "OTP expired. Request again.", Toast.LENGTH_SHORT).show();
                    finish();}}
        }.start();

        TextView phno = findViewById(R.id.otp_txt_send);
        EditText one = findViewById(R.id.otp_edt_one);
        EditText two = findViewById(R.id.otp_edt_two);
        EditText three = findViewById(R.id.otp_edt_three);
        EditText four = findViewById(R.id.otp_edt_four);
        EditText five = findViewById(R.id.otp_edt_five);
        EditText six = findViewById(R.id.otp_edt_six);
        Button verify = findViewById(R.id.otp_btn_verify);

        phno.setText(String.format(
                "We have sent an OTP to " + getIntent().getStringExtra("country") + " " + getIntent().getStringExtra("mobile")
        ));
        verificationID = getIntent().getStringExtra("verificationID");

        verify.setOnClickListener(view -> {
            verifyDialog = new ProgressDialog(OtpActivity.this);
            verifyDialog.setMessage("Verifying");
            verifyDialog.setCanceledOnTouchOutside (false);
            verifyDialog.setCancelable (false);
            verifyDialog.show();

            if (one.getText().toString().trim().isEmpty() ||
                    two.getText().toString().trim().isEmpty() ||
                    three.getText().toString().trim().isEmpty() ||
                    four.getText().toString().trim().isEmpty() ||
                    five.getText().toString().trim().isEmpty() ||
                    six.getText().toString().trim().isEmpty()) {
                Toast.makeText(OtpActivity.this, "OTP is not Valid", Toast.LENGTH_SHORT).show();
            } else if (verificationID != null) {
                String code = one.getText().toString().trim()
                        + two.getText().toString().trim()
                        + three.getText().toString().trim()
                        + four.getText().toString().trim()
                        + five.getText().toString().trim()
                        + six.getText().toString().trim();

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationID, code);
                FirebaseAuth.getInstance()
                        .signInWithCredential(credential)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                success.set(1);
                                String currentUid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.child(currentUid).exists()){
                                            if (verifyDialog.isShowing()){
                                                verifyDialog.dismiss();
                                            }
                                            Intent i = new Intent(OtpActivity.this, SwipeActivity.class);
                                            startActivity(i);
                                            finishAffinity();
                                        } else {
                                            if (verifyDialog.isShowing()){
                                                verifyDialog.dismiss();
                                            }
                                            Intent i = new Intent(OtpActivity.this, WelcomeActivity.class);
                                            startActivity(i);
                                            finishAffinity();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Toast.makeText(OtpActivity.this, "OTP is not Valid", Toast.LENGTH_SHORT).show();
                            }
                        });
                }
            });
    }

    private void edittextInput() {
        TextView one = findViewById(R.id.otp_edt_one);
        TextView two = findViewById(R.id.otp_edt_two);
        TextView three = findViewById(R.id.otp_edt_three);
        TextView four = findViewById(R.id.otp_edt_four);
        TextView five = findViewById(R.id.otp_edt_five);
        TextView six = findViewById(R.id.otp_edt_six);

        one.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                two.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        two.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                three.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        three.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                four.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        four.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                five.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        five.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                six.requestFocus();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }
}