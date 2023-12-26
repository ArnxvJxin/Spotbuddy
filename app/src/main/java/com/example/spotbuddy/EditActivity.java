package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class EditActivity extends AppCompatActivity {

    private final int PHOTO1_REQ_CODE = 1001;
    private final int PHOTO2_REQ_CODE = 1002;
    private final int PHOTO3_REQ_CODE = 1003;
    private final int PHOTO4_REQ_CODE = 1004;
    private int GALLERY_PERMISSION_CODE = 2;
    ProgressDialog loadDialog, progressDialog;
    String uid_txt, photo1_txt, photo2_txt, photo3_txt, photo4_txt, bio_change,profession_change, college_change, school_change, interests_change, photo1_change, photo2_change, photo3_change, photo4_change;
    TextView cancel, save, name, birthday, mbti;
    ArrayList< Uri > uriList = new ArrayList < > ();
    ArrayList<String> urlList_txt = new ArrayList < > ();
    EditText bio, profession, college, school, interests;
    ImageButton photo1, photo2, photo3, photo4;
    Uri img1 = null, img2 = null, img3 = null, img4 = null;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        loadDialog = new ProgressDialog(EditActivity.this);
        loadDialog.setMessage("Updating Profile.");
        loadDialog.setCanceledOnTouchOutside (false);
        loadDialog.setCancelable (false);
        loadDialog.show();

        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        retrieveData();

        cancel = findViewById(R.id.edit_txt_cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        save = findViewById(R.id.edit_txt_save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog = new ProgressDialog(EditActivity.this);
                progressDialog.setMessage("Updating Profile.");
                progressDialog.setCanceledOnTouchOutside (false);
                progressDialog.setCancelable (false);
                progressDialog.show();

                uploadData();
            }
        });

        name = findViewById(R.id.edit_txt_nametxt);
        birthday = findViewById(R.id.edit_txt_birthdaytxt);
        mbti = findViewById(R.id.edit_txt_mbtitxt);
        bio = findViewById(R.id.edit_edt_bio);
        profession = findViewById(R.id.edit_edt_profession);
        college = findViewById(R.id.edit_edt_college);
        school = findViewById(R.id.edit_edt_school);
        interests = findViewById(R.id.edit_edt_interests);
        photo1 = findViewById(R.id.edit_ibt_one);
        photo2 = findViewById(R.id.edit_ibt_two);
        photo3 = findViewById(R.id.edit_ibt_three);
        photo4 = findViewById(R.id.edit_ibt_four);

        photo1.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iphoto1 = new Intent(Intent.ACTION_PICK);
                iphoto1.setData((MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                startActivityForResult(iphoto1, PHOTO1_REQ_CODE);
            }
        }));

        photo2.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iphoto2 = new Intent(Intent.ACTION_PICK);
                iphoto2.setData((MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                startActivityForResult(iphoto2, PHOTO2_REQ_CODE);
            }
        }));

        photo3.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iphoto3 = new Intent(Intent.ACTION_PICK);
                iphoto3.setData((MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                startActivityForResult(iphoto3, PHOTO3_REQ_CODE);
            }
        }));

        photo4.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent iphoto4 = new Intent(Intent.ACTION_PICK);
                iphoto4.setData((MediaStore.Images.Media.EXTERNAL_CONTENT_URI));
                startActivityForResult(iphoto4, PHOTO4_REQ_CODE);
            }
        }));
    }

    private void saveData() {
        photo1_change = photo1_txt;
        photo2_change = photo2_txt;
        photo3_change = photo3_txt;
        photo4_change = photo4_txt;

        if (urlList_txt.size() == 4) {
            if (!Objects.equals(urlList_txt.get(0), String.valueOf(0))) {
                photo1_change = urlList_txt.get(0).trim();
            }

            if (!Objects.equals(urlList_txt.get(1), String.valueOf(0))) {
                photo2_change = urlList_txt.get(1).trim();
            }

            if (!Objects.equals(urlList_txt.get(2), String.valueOf(0))) {
                photo3_change = urlList_txt.get(2).trim();
            }

            if (!Objects.equals(urlList_txt.get(3), String.valueOf(0))) {
                photo4_change = urlList_txt.get(3).trim();
            }

            if (photo1_change == null || photo2_change == null || photo3_change == null || photo4_change == null) {
                Toast.makeText(EditActivity.this, "Select 4 photos", Toast.LENGTH_SHORT).show();
            } else {
                reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        reference.child("Users").child(uid_txt).child("Bio").setValue(bio_change);
                        reference.child("Users").child(uid_txt).child("Profession").setValue(profession_change);
                        reference.child("Users").child(uid_txt).child("College").setValue(college_change);
                        reference.child("Users").child(uid_txt).child("School").setValue(school_change);
                        reference.child("Users").child(uid_txt).child("Interests").setValue(interests_change);
                        reference.child("Users").child(uid_txt).child("Photo1").setValue(photo1_change);
                        reference.child("Users").child(uid_txt).child("Photo2").setValue(photo2_change);
                        reference.child("Users").child(uid_txt).child("Photo3").setValue(photo3_change);
                        reference.child("Users").child(uid_txt).child("Photo4").setValue(photo4_change);

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(EditActivity.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }
    }

    private void uploadData() {

        bio_change = bio.getText().toString().trim();
        profession_change = profession.getText().toString().trim();
        college_change = college.getText().toString().trim();
        school_change = school.getText().toString().trim();
        interests_change = interests.getText().toString().trim();

        if (img1 == null) {
            img1 = Uri.parse(photo1_txt);
        }

        if (img2 == null) {
            img2 = Uri.parse(photo2_txt);
        }

        if (img3 == null) {
            img3 = Uri.parse(photo3_txt);
        }

        if (img4 == null) {
            img4 = Uri.parse(photo4_txt);
        }

        uriList.add(0, img1);
        uriList.add(1, img2);
        uriList.add(2, img3);
        uriList.add(3, img4);

        for (int j = 0; j < 4; j++) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss.SSS", Locale.US);
            Date now = new Date();
            String imageName = formatter.format(now) + "_" + uid_txt;
            StorageReference imageRef = storageReference.child(uid_txt + "/" + imageName);
            Uri allimages = uriList.get(j);
            int finalJ = j;
            if (uriList.get(finalJ) == null || uriList.get(finalJ).toString().trim().startsWith("https")) {
                urlList_txt.add(String.valueOf(0));
                saveData();
            } else {
                imageRef.putFile(allimages).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                urlList_txt.add(uri.toString());
                                saveData();
                            }
                        });
                    }
                });
            }
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
                        String name_txt = String.valueOf(dataSnapshot.child("Name").getValue());
                        String day_txt = String.valueOf(dataSnapshot.child("Birthday").getValue());
                        String mbti_txt = String.valueOf(dataSnapshot.child("MBTI").getValue());
                        String month_txt = String.valueOf(dataSnapshot.child("Birthmonth").getValue());
                        String year_txt = String.valueOf(dataSnapshot.child("Birthyear").getValue());
                        String bio_txt = String.valueOf(dataSnapshot.child("Bio").getValue());
                        String profession_txt = String.valueOf(dataSnapshot.child("Profession").getValue());
                        String college_txt = String.valueOf(dataSnapshot.child("College").getValue());
                        String school_txt = String.valueOf(dataSnapshot.child("School").getValue());
                        String interests_txt = String.valueOf(dataSnapshot.child("Interests").getValue());
                        photo1_txt = String.valueOf(dataSnapshot.child("Photo1").getValue());
                        photo2_txt = String.valueOf(dataSnapshot.child("Photo2").getValue());
                        photo3_txt = String.valueOf(dataSnapshot.child("Photo3").getValue());
                        photo4_txt = String.valueOf(dataSnapshot.child("Photo4").getValue());


                        Integer age = 0;
                        try {
                            LocalDate today = LocalDate.now();
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                            LocalDate birthdate = LocalDate.parse(day_txt.trim() +
                                    month_txt.trim() +
                                    year_txt.trim(), formatter);
                            age = Period.between(birthdate, today).getYears();
                        } catch (DateTimeParseException e) {
                            e.printStackTrace();
                        }
                        String age_txt = age.toString();

                        name.setText(name_txt);
                        birthday.setText(String.format("%s/%s/%s (%s years)", day_txt, month_txt, year_txt, age_txt));
                        mbti.setText(mbti_txt);
                        if (!bio_txt.equals("null"))
                            bio.setText(bio_txt);
                        if (!profession_txt.equals("null"))
                            profession.setText(profession_txt);
                        if (!college_txt.equals("null"))
                            college.setText(college_txt);
                        if (!school_txt.equals("null"))
                            school.setText(school_txt);
                        if (!interests_txt.equals("null"))
                            interests.setText(interests_txt);

                        Picasso.get().load(photo1_txt).into(photo1);
                        Picasso.get().load(photo2_txt).into(photo2);
                        Picasso.get().load(photo3_txt).into(photo3);
                        Picasso.get().load(photo4_txt).into(photo4);

                        if (loadDialog.isShowing()) {
                            loadDialog.dismiss();
                        }

                    }
                }
                else {
                    if (loadDialog.isShowing()) {
                        loadDialog.dismiss();
                    }
                    Toast.makeText(EditActivity.this, "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO1_REQ_CODE) {
                assert data != null;
                img1 = data.getData();
                photo1.setImageURI(img1);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO2_REQ_CODE) {
                assert data != null;
                img2 = data.getData();
                photo2.setImageURI(img2);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO3_REQ_CODE) {
                assert data != null;
                img3 = data.getData();
                photo3.setImageURI(img3);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO4_REQ_CODE) {
                assert data != null;
                img4 = data.getData();
                photo4.setImageURI(img4);
            }
        }
    }
}