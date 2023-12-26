package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.slider.RangeSlider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    ProgressDialog updatingDialog, loadingDialog, snoozeDialog, unsnoozeDialog, logoutDialog, deleteDialog;
    TextView cancel, save, status, gender, preference, agestart, ageend, distance, location, phone;
    RadioGroup preference_rag, distance_rag;
    RangeSlider age_sld;
    EditText email;
    Button snooze, unsnooze, logout, delete;
    String uid_txt;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    FirebaseAuth auth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        retrieveData();

        loadingDialog = new ProgressDialog(SettingsActivity.this);
        loadingDialog.setMessage("Loading.");
        loadingDialog.setCanceledOnTouchOutside (false);
        loadingDialog.setCancelable (false);
        loadingDialog.show();

        cancel = findViewById(R.id.settings_txt_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save = findViewById(R.id.settings_txt_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatingDialog = new ProgressDialog(SettingsActivity.this);
                updatingDialog.setMessage("Updating");
                updatingDialog.setCanceledOnTouchOutside (false);
                updatingDialog.setCancelable (false);
                updatingDialog.show();

                saveData();
            }
        });

        status = findViewById(R.id.settings_txt_statustxt);
        gender = findViewById(R.id.settings_txt_gendertxt);
        preference = findViewById(R.id.settings_txt_preferencetxt);
        agestart = findViewById(R.id.settings_txt_agestart);
        ageend = findViewById(R.id.settings_txt_ageend);
        distance = findViewById(R.id.settings_txt_distancetxt);
        location = findViewById(R.id.settings_txt_locationtxt);
        phone = findViewById(R.id.settings_txt_phonetxt);
        preference_rag = findViewById(R.id.settings_rag_preference);
        distance_rag = findViewById(R.id.settings_rag_distance);
        age_sld = findViewById(R.id.settings_sld_age);
        email = findViewById(R.id.settings_edt_email);
        snooze = findViewById(R.id.settings_btn_snooze);
        unsnooze = findViewById(R.id.settings_btn_unsnooze);
        logout = findViewById(R.id.settings_btn_logout);
        delete = findViewById(R.id.settings_btn_delete);

        snooze.setVisibility(View.GONE);
        unsnooze.setVisibility(View.GONE);

        snooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder snoozeAlert = new AlertDialog.Builder(SettingsActivity.this);
                snoozeAlert.setMessage("Do you want to snooze your account").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                snoozeDialog = new ProgressDialog(SettingsActivity.this);
                                snoozeDialog.setMessage("Snoozing Account");
                                snoozeDialog.setCanceledOnTouchOutside (false);
                                snoozeDialog.setCancelable (false);
                                snoozeDialog.show();
                                reference.child("Users").child(uid_txt).child("Status").setValue("Invisible");
                                status.setText("Invisible");
                                snooze.setVisibility(View.GONE);
                                unsnooze.setVisibility(View.VISIBLE);
                                if (snoozeDialog.isShowing()) {
                                    snoozeDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "You are now invisible to others", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                snoozeAlert.create();
                snoozeAlert.show();
            }
        });

        unsnooze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder unsnoozeAlert = new AlertDialog.Builder(SettingsActivity.this);
                unsnoozeAlert.setMessage("Do you want to unsnooze your account").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                unsnoozeDialog = new ProgressDialog(SettingsActivity.this);
                                unsnoozeDialog.setMessage("Unsnoozing Account");
                                unsnoozeDialog.setCanceledOnTouchOutside (false);
                                unsnoozeDialog.setCancelable (false);
                                unsnoozeDialog.show();
                                reference.child("Users").child(uid_txt).child("Status").setValue("Visible");
                                status.setText("Visible");
                                unsnooze.setVisibility(View.GONE);
                                snooze.setVisibility(View.VISIBLE);
                                if (unsnoozeDialog.isShowing()) {
                                    unsnoozeDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "You are now visible to others", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                unsnoozeAlert.create();
                unsnoozeAlert.show();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder logoutAlert = new AlertDialog.Builder(SettingsActivity.this);
                logoutAlert.setMessage("Are you sure you want to logout?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                logoutDialog = new ProgressDialog(SettingsActivity.this);
                                logoutDialog.setMessage("Logging Out");
                                logoutDialog.setCanceledOnTouchOutside (false);
                                logoutDialog.setCancelable (false);
                                logoutDialog.show();
                                auth.signOut();
                                Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(i);
                                if (logoutDialog.isShowing()) {
                                    logoutDialog.dismiss();
                                    Toast.makeText(SettingsActivity.this, "Logged Out", Toast.LENGTH_SHORT).show();
                                    finishAffinity();
                                }
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                logoutAlert.create();
                logoutAlert.show();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder deleteAlert = new AlertDialog.Builder(SettingsActivity.this);
                deleteAlert.setMessage("Are you sure you want to delete the account?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                deleteDialog = new ProgressDialog(SettingsActivity.this);
                                deleteDialog.setMessage("Deleting Account");
                                deleteDialog.setCanceledOnTouchOutside (false);
                                deleteDialog.setCancelable (false);
                                deleteDialog.show();
                                Objects.requireNonNull(auth.getCurrentUser()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        reference.child("Users").child(uid_txt).child("Status").setValue("Deleted");
                                        Intent i = new Intent(SettingsActivity.this, LoginActivity.class);
                                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        if (deleteDialog.isShowing()) {
                                            deleteDialog.dismiss();
                                            Toast.makeText(SettingsActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                            finishAffinity();
                                        }

                                    }
                                });
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                deleteAlert.create();
                deleteAlert.show();

            }
        });
    }

    private void listenData() {

        preference_rag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                preference.setText(selectedRadioButton.getText().toString());
            }
        });

        distance_rag.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = findViewById(selectedRadioButtonId);
                distance.setText(selectedRadioButton.getText().toString());
            }
        });

        age_sld.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull RangeSlider slider, float value, boolean fromUser) {
                agestart.setText(String.valueOf(Math.round(slider.getValues().get(0))).trim());
                ageend.setText(String.valueOf(Math.round(slider.getValues().get(1))).trim());
            }
        });

    }

    private void saveData() {

        String preference_change = preference.getText().toString().trim();
        String agestart_change = agestart.getText().toString().trim();
        String ageend_change = ageend.getText().toString().trim();
        String distance_change = distance.getText().toString().trim();
        String email_change = email.getText().toString().trim();

        if (email_change.equals("")) {
            Toast.makeText(SettingsActivity.this, "Please provide an email", Toast.LENGTH_SHORT).show();
        } else {
            reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    reference.child("Users").child(uid_txt).child("Preference").setValue(preference_change);
                    reference.child("Users").child(uid_txt).child("Age Start").setValue(agestart_change);
                    reference.child("Users").child(uid_txt).child("Age End").setValue(ageend_change);
                    reference.child("Users").child(uid_txt).child("Distance").setValue(distance_change);
                    reference.child("Users").child(uid_txt).child("Email").setValue(email_change);

                    if (updatingDialog.isShowing()) {
                        updatingDialog.dismiss();
                    }

                    Toast.makeText(SettingsActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    private void retrieveData() {
        reference.child("Users").child(uid_txt).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String status_txt = String.valueOf(dataSnapshot.child("Status").getValue());
                        String gender_txt = String.valueOf(dataSnapshot.child("Gender").getValue());
                        String preference_txt = String.valueOf(dataSnapshot.child("Preference").getValue());
                        String agestart_txt = String.valueOf(dataSnapshot.child("Age Start").getValue());
                        String ageend_txt = String.valueOf(dataSnapshot.child("Age End").getValue());
                        String distance_txt = String.valueOf(dataSnapshot.child("Distance").getValue());
                        String city_txt = String.valueOf(dataSnapshot.child("City").getValue());
                        String state_txt = String.valueOf(dataSnapshot.child("State").getValue());
                        String country_txt = String.valueOf(dataSnapshot.child("Country").getValue());
                        String email_txt = String.valueOf(dataSnapshot.child("Email").getValue());
                        String phone_txt = String.valueOf(dataSnapshot.child("Phone").getValue());

                        if (!status_txt.equals("null")) {
                            status.setText(status_txt);

                            if (status.getText().toString().equals("Invisible")) {
                                snooze.setVisibility(View.GONE);
                                unsnooze.setVisibility(View.VISIBLE);
                            } else if (status.getText().toString().equals("Visible")) {
                                unsnooze.setVisibility(View.GONE);
                                snooze.setVisibility(View.VISIBLE);
                            }
                        } else {
                            snooze.setVisibility(View.GONE);
                            unsnooze.setVisibility(View.GONE);
                        }

                        if (!gender_txt.equals("null"))
                            gender.setText(gender_txt);
                        if (!preference_txt.equals("null")) {
                            preference.setText(preference_txt);
                            int radioButtonCount = preference_rag.getChildCount();
                            for (int i = 0; i < radioButtonCount; i++) {
                                RadioButton radioButton = (RadioButton) preference_rag.getChildAt(i);
                                if (radioButton.getText().toString().trim().equals(preference_txt)) {
                                    preference_rag.check(radioButton.getId());
                                    break;
                                }
                            }
                        }
                        if (!agestart_txt.equals("null") || !ageend_txt.equals("null")) {
                            agestart.setText(agestart_txt);
                            ageend.setText(ageend_txt);
                            age_sld.setValues(Float.parseFloat(agestart_txt),Float.parseFloat(ageend_txt));
                        }
                        if (!distance_txt.equals("null")) {
                            distance.setText(distance_txt);
                            int radioButtonCount = distance_rag.getChildCount();
                            for (int i = 0; i < radioButtonCount; i++) {
                                RadioButton radioButton = (RadioButton) distance_rag.getChildAt(i);
                                if (radioButton.getText().toString().trim().equals(distance_txt)) {
                                    distance_rag.check(radioButton.getId());
                                    break;
                                }
                            }
                        }
                        if (!city_txt.equals("null") || !state_txt.equals("null") || !country_txt.equals("null"))
                            location.setText(String.format("%s, %s, %s", city_txt, state_txt, country_txt));
                        if (!email_txt.equals("null"))
                            email.setText(email_txt);
                        if (!phone_txt.equals("null"))
                            phone.setText(phone_txt);

                        if (loadingDialog.isShowing()) {
                            loadingDialog.dismiss();
                        }
                    }
                }
                else {
                    if (loadingDialog.isShowing()) {
                        loadingDialog.dismiss();
                    }

                    Toast.makeText(SettingsActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }

                listenData();
            }
        });
    }
}