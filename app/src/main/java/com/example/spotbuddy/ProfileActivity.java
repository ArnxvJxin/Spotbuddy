package com.example.spotbuddy;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;


public class ProfileActivity extends AppCompatActivity {
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    private long pressedTime;
    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_DAY = "day";
    public static final String EXTRA_MONTH = "month";
    public static final String EXTRA_YEAR = "year";
    public static final String EXTRA_AGESTART = "start";
    public static final String EXTRA_AGEEND = "end";
    public static final String EXTRA_GENDER = "gender";
    public static final String EXTRA_PREFERENCE = "preference";
    public static final String EXTRA_EMAIL = "email";
    public static final String EXTRA_MBTI = "mbti";
    String agestart_txt, ageend_txt, personality_txt;
    TextView mbti;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        EditText name = findViewById(R.id.profile_edt_name);
        EditText day = findViewById(R.id.profile_edt_day);
        EditText month = findViewById(R.id.profile_edt_month);
        EditText year = findViewById(R.id.profile_edt_year);
        EditText email = findViewById(R.id.profile_edt_email);
        RadioGroup rggender = findViewById(R.id.profile_rag_gender);
        RadioGroup rgpreference = findViewById(R.id.profile_rag_preference);
        Button personality = findViewById(R.id.profile_btn_mbti);
        Button next = findViewById(R.id.profile_btn_next);
        mbti = findViewById(R.id.profile_txt_mbti);

        ageCalculator();

        personality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                personality.setEnabled(false);
                Intent i = new Intent(ProfileActivity.this, PersonalityActivity.class);
                startActivityForResult(i,1);
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                RadioButton radgender, radpreference;
                String radgender_txt = "", radpreference_txt = "";
                Integer genderId = rggender.getCheckedRadioButtonId();
                radgender = findViewById(genderId);
                Integer preferenceId = rgpreference.getCheckedRadioButtonId();
                radpreference = findViewById(preferenceId);
                String uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                String phone_txt = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                String name_txt = name.getText().toString().trim();
                String day_txt = day.getText().toString().trim();
                String month_txt = month.getText().toString().trim();
                String year_txt = year.getText().toString().trim();
                String mbti_txt = mbti.getText().toString().trim();
                String email_txt = email.getText().toString().trim();
                if(radgender != null) {
                    radgender_txt = radgender.getText().toString().trim();
                }
                if(radpreference != null) {
                    radpreference_txt = radpreference.getText().toString().trim();
                }

                if (name_txt.equals("") || day_txt.equals("") || month_txt.equals("") || year_txt.equals("") ||
                        radgender_txt.equals("") || radpreference_txt.equals("") || email_txt.equals("") || mbti_txt.equals("")) {
                    Toast.makeText(ProfileActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (day.length() == 2 && month.length() == 2 && year.length() == 4) {
                    int age_text = 0;
                    try {
                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate birthdate = LocalDate.parse(day.getText().toString().trim() +
                                month.getText().toString().trim() +
                                year.getText().toString().trim(), formatter);
                        age_text = Period.between(birthdate, today).getYears();
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    if (age_text > 0) {
                        if (age_text < 18 || age_text > 60) {
                            Toast.makeText(ProfileActivity.this, "Pleas enter age age between 18 and 60.", Toast.LENGTH_SHORT).show();
                        } else {
                            if (age_text == 18) {
                                agestart_txt = String.valueOf(18);
                                ageend_txt = String.valueOf(20);
                            } else if (age_text == 19) {
                                agestart_txt = String.valueOf(18);
                                ageend_txt = String.valueOf(21);
                            } else if (age_text == 59) {
                                agestart_txt = String.valueOf(57);
                                ageend_txt = String.valueOf(60);
                            } else if (age_text == 60) {
                                agestart_txt = String.valueOf(58);
                                ageend_txt = String.valueOf(60);
                            } else{
                                agestart_txt = String.valueOf(age_text-2);
                                ageend_txt = String.valueOf(age_text+2);
                            }

                            Intent i = new Intent(ProfileActivity.this, PhotoActivity.class);
                            i.putExtra(EXTRA_NAME,name_txt);
                            i.putExtra(EXTRA_DAY,day_txt);
                            i.putExtra(EXTRA_MONTH,month_txt);
                            i.putExtra(EXTRA_YEAR,year_txt);
                            i.putExtra(EXTRA_AGESTART,agestart_txt);
                            i.putExtra(EXTRA_AGEEND,ageend_txt);
                            i.putExtra(EXTRA_GENDER,radgender_txt);
                            i.putExtra(EXTRA_PREFERENCE,radpreference_txt);
                            i.putExtra(EXTRA_EMAIL,email_txt);
                            i.putExtra(EXTRA_MBTI, personality_txt);
                            startActivity(i);
                            }
                        } else {
                            Toast.makeText(ProfileActivity.this, "Invalid Date", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void ageCalculator() {
        EditText day = findViewById(R.id.profile_edt_day);
        EditText month = findViewById(R.id.profile_edt_month);
        EditText year = findViewById(R.id.profile_edt_year);
        TextView age = findViewById(R.id.profile_txt_age);

        day.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int dlength = day.length();
                int mlength = month.length();
                int ylength = year.length();

                if (dlength == 2) {
                    month.requestFocus();
                }
                if (dlength == 2 && mlength == 2 && ylength == 4) {
                    int age_text = 0;
                    try {
                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate birthdate = LocalDate.parse(day.getText().toString().trim() +
                                month.getText().toString().trim() +
                                year.getText().toString().trim(), formatter);
                        age_text = Period.between(birthdate, today).getYears();
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    if (age_text > 0) {
                        if (age_text < 18 || age_text > 60) {
                            age.setText(MessageFormat.format("Your age is {0} years. Pleas enter age between 18 and 60.", age_text));
                        } else {
                            age.setText(MessageFormat.format("Your age is {0} years. You won't be able to change it later.", age_text));
                        }
                    } else {
                        age.setText("Invalid Date");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        month.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int dlength = day.length();
                int mlength = month.length();
                int ylength = year.length();

                if (mlength == 2) {
                    year.requestFocus();
                }
                if (dlength == 2 && mlength == 2 && ylength == 4) {
                    int age_text = 0;
                    try {
                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate birthdate = LocalDate.parse(day.getText().toString().trim() +
                                month.getText().toString().trim() +
                                year.getText().toString().trim(), formatter);
                        age_text = Period.between(birthdate, today).getYears();
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    if (age_text > 0) {
                        if (age_text < 18 || age_text > 60) {
                            age.setText(MessageFormat.format("Your age is {0} years. Pleas enter age between 18 and 60.", age_text));
                        } else {
                            age.setText(MessageFormat.format("Your age is {0} years. You won't be able to change it later.", age_text));
                        }
                    } else {
                        age.setText("Invalid Date");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int dlength = day.length();
                int mlength = month.length();
                int ylength = year.length();

                if (dlength == 2 && mlength == 2 && ylength == 4) {
                    int age_text = 0;
                    try {
                        LocalDate today = LocalDate.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                        LocalDate birthdate = LocalDate.parse(day.getText().toString().trim() +
                                month.getText().toString().trim() +
                                year.getText().toString().trim(), formatter);
                        age_text = Period.between(birthdate, today).getYears();
                    } catch (DateTimeParseException e) {
                        e.printStackTrace();
                    }
                    if (age_text > 0) {
                        if (age_text < 18 || age_text > 60) {
                            age.setText(MessageFormat.format("Your age is {0} years. Pleas enter age age between 18 and 60.", age_text));
                        } else {
                            age.setText(MessageFormat.format("Your age is {0} years. You won't be able to change it later.", age_text));
                        }
                    } else {
                        age.setText("Invalid Date");
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Check if there is a string result from the SecondActivity
        if (getIntent().hasExtra("personality")) {
            personality_txt = getIntent().getStringExtra("personality").trim();
            this.mbti.setText(personality_txt);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the result code matches the one we used when starting the SecondActivity
        if (requestCode == 1 && resultCode == RESULT_OK) {
            personality_txt = data.getStringExtra("personality").trim();
            this.mbti.setText(personality_txt);
        }
    }

}