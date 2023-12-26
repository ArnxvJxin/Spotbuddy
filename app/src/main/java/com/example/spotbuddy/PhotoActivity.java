package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class PhotoActivity extends AppCompatActivity {

    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users");

    private final int GALLERY_REQ_CODE = 1000;
    private final int PHOTO1_REQ_CODE = 1001;
    private final int PHOTO2_REQ_CODE = 1002;
    private final int PHOTO3_REQ_CODE = 1003;
    private final int PHOTO4_REQ_CODE = 1004;
    private int GALLERY_PERMISSION_CODE = 2;
    ImageButton photo1, photo2, photo3, photo4;
    ProgressDialog uploadDialog;
    Uri img1, img2, img3, img4;
    ArrayList < Uri > uriList = new ArrayList < > ();
    ArrayList < Uri > urlList_txt = new ArrayList < > ();
    TextView more;
    AppCompatButton gallery;
    String photo1_txt, photo2_txt, photo3_txt, photo4_txt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Button next = findViewById(R.id.photo_btn_next);
        gallery = findViewById(R.id.photo_box_image);
        photo1 = findViewById(R.id.edit_ibt_one);
        photo2 = findViewById(R.id.edit_ibt_two);
        photo3 = findViewById(R.id.edit_ibt_three);
        photo4 = findViewById(R.id.edit_ibt_four);
        more = findViewById(R.id.photo_txt_more);

        Intent intent = getIntent();
        String uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        String phone_txt = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
        String name_txt = intent.getStringExtra(ProfileActivity.EXTRA_NAME);
        String day_txt = intent.getStringExtra(ProfileActivity.EXTRA_DAY);
        String month_txt = intent.getStringExtra(ProfileActivity.EXTRA_MONTH);
        String year_txt = intent.getStringExtra(ProfileActivity.EXTRA_YEAR);
        String agestart_txt = intent.getStringExtra(ProfileActivity.EXTRA_AGESTART);
        String ageend_txt = intent.getStringExtra(ProfileActivity.EXTRA_AGEEND);
        String radgender_txt = intent.getStringExtra(ProfileActivity.EXTRA_GENDER);
        String radpreference_txt = intent.getStringExtra(ProfileActivity.EXTRA_PREFERENCE);
        String email_txt = intent.getStringExtra(ProfileActivity.EXTRA_EMAIL);
        String mbti_txt = intent.getStringExtra(ProfileActivity.EXTRA_MBTI);

        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent igallery = new Intent(Intent.ACTION_PICK);
                    igallery.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(igallery, GALLERY_REQ_CODE);
            }
        });

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

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (img1 == null || img2 == null || img3 == null || img4 == null) {
                    Toast.makeText(PhotoActivity.this, "Please add 4 images", Toast.LENGTH_SHORT).show();
                } else {
                    uploadDialog = new ProgressDialog(PhotoActivity.this);
                    uploadDialog.setMessage("Creating Profile");
                    uploadDialog.setCanceledOnTouchOutside (false);
                    uploadDialog.setCancelable (false);
                    uploadDialog.show();

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
                        imageRef.putFile(allimages).addOnSuccessListener(new OnSuccessListener < UploadTask.TaskSnapshot > () {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener < Uri > () {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        urlList_txt.add(uri);
                                        if (urlList_txt.size() == 4) {
                                            photo1_txt = urlList_txt.get(0).toString().trim();
                                            photo2_txt = urlList_txt.get(1).toString().trim();
                                            photo3_txt = urlList_txt.get(2).toString().trim();
                                            photo4_txt = urlList_txt.get(3).toString().trim();

                                            if (photo1_txt.equals("") || photo2_txt.equals("") || photo3_txt.equals("") || photo4_txt.equals("")) {
                                                if (uploadDialog.isShowing()) {
                                                    uploadDialog.dismiss();
                                                }
                                                Toast.makeText(PhotoActivity.this, "Please Select 4 Photos", Toast.LENGTH_SHORT).show();
                                            } else {
                                                SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy HH_mm_ss.SSS", Locale.US);
                                                Date now = new Date();
                                                String matchtime = formatter.format(now) + "_" + uid_txt;

                                                databaseReference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                        databaseReference.child("Users").child(uid_txt).child("UID").setValue(uid_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Phone").setValue(phone_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Name").setValue(name_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Birthday").setValue(day_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Birthmonth").setValue(month_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Birthyear").setValue(year_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Age Start").setValue(agestart_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Age End").setValue(ageend_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Gender").setValue(radgender_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Preference").setValue(radpreference_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Distance").setValue("City");
                                                        databaseReference.child("Users").child(uid_txt).child("Email").setValue(email_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Photo1").setValue(photo1_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Photo2").setValue(photo2_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Photo3").setValue(photo3_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Photo4").setValue(photo4_txt);
                                                        databaseReference.child("Users").child(uid_txt).child("Swipes").child(uid_txt).setValue(String.valueOf(0));
                                                        databaseReference.child("Users").child(uid_txt).child("Status").setValue("Visible");
                                                        databaseReference.child("Users").child(uid_txt).child("MBTI").setValue(mbti_txt);

                                                        if (uploadDialog.isShowing()) {
                                                            uploadDialog.dismiss();
                                                        }

                                                        Toast.makeText(PhotoActivity.this, "Profile Created Successfully", Toast.LENGTH_SHORT).show();
                                                        Intent i = new Intent(PhotoActivity.this, LocationActivity.class);
                                                        startActivity(i);
                                                        finishAffinity();
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {

                                                    }
                                                });
                                            }
                                        }
                                    }
                                });
                            }
                        });
                    }
                }


            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == GALLERY_REQ_CODE) {
                assert data != null;
                img1 = data.getData();
                photo1.setImageURI(img1);
                photo1.setVisibility(View.VISIBLE);
                photo2.setVisibility(View.VISIBLE);

                more.setVisibility(View.VISIBLE);
                gallery.setEnabled(false);

            }
        }

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
                photo3.setVisibility(View.VISIBLE);
            }
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == PHOTO3_REQ_CODE) {
                assert data != null;
                img3 = data.getData();
                photo3.setImageURI(img3);
                photo4.setVisibility(View.VISIBLE);
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