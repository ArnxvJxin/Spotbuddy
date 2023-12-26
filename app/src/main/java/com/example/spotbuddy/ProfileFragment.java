package com.example.spotbuddy;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class ProfileFragment extends Fragment {

    ImageButton settings;
    ImageView photo1, photo2, photo3, photo4;
    TextView edit, location, name, mbti, bio, profession, college, school, interests;
    String uid_txt;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("Users");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        retrieveData();

        settings = view.findViewById(R.id.profile_fra_ibt_settings);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), SettingsActivity.class);
                startActivity(i);
            }
        });

        edit = view.findViewById(R.id.profile_fra_txt_edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getActivity(), EditActivity.class);
                startActivity(i);
            }
        });

        location =view.findViewById(R.id.profile_fra_txt_location);
        name =view.findViewById(R.id.profile_fra_txt_name);
        profession =view.findViewById(R.id.profile_fra_txt_profession);
        college =view.findViewById(R.id.profile_fra_txt_college);
        school =view.findViewById(R.id.profile_fra_txt_school);
        mbti =view.findViewById(R.id.profile_fra_txt_mbtitxt);
        bio =view.findViewById(R.id.profile_fra_txt_biotxt);
        interests =view.findViewById(R.id.profile_fra_txt_intereststxt);
        photo1 =view.findViewById(R.id.profile_fra_img_one);
        photo2 =view.findViewById(R.id.profile_fra_img_two);
        photo3 =view.findViewById(R.id.profile_fra_img_three);
        photo4 =view.findViewById(R.id.profile_fra_img_four);


        return view;
    }

    private void retrieveData() {
        reference.child("Users").child(uid_txt).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    if(task.getResult().exists()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        String city_txt = String.valueOf(dataSnapshot.child("City").getValue());
                        String state_txt = String.valueOf(dataSnapshot.child("State").getValue());
                        String name_txt = String.valueOf(dataSnapshot.child("Name").getValue());
                        String day_txt = String.valueOf(dataSnapshot.child("Birthday").getValue());
                        String month_txt = String.valueOf(dataSnapshot.child("Birthmonth").getValue());
                        String year_txt = String.valueOf(dataSnapshot.child("Birthyear").getValue());
                        String mbti_txt = String.valueOf(dataSnapshot.child("MBTI").getValue());
                        String bio_txt = String.valueOf(dataSnapshot.child("Bio").getValue());
                        String profession_txt = String.valueOf(dataSnapshot.child("Profession").getValue());
                        String college_txt = String.valueOf(dataSnapshot.child("College").getValue());
                        String school_txt = String.valueOf(dataSnapshot.child("School").getValue());
                        String interests_txt = String.valueOf(dataSnapshot.child("Interests").getValue());
                        String photo1_txt = String.valueOf(dataSnapshot.child("Photo1").getValue());
                        String photo2_txt = String.valueOf(dataSnapshot.child("Photo2").getValue());
                        String photo3_txt = String.valueOf(dataSnapshot.child("Photo3").getValue());
                        String photo4_txt = String.valueOf(dataSnapshot.child("Photo4").getValue());

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

                        if (!city_txt.equals("null") || !state_txt.equals("null"))
                            location.setText(String.format("%s, %s", city_txt, state_txt));
                        if (!name_txt.equals("null") || !age_txt.equals("null"))
                            name.setText(String.format("%s, %s", name_txt, age_txt));
                        if (!mbti_txt.equals("null"))
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

                    }
                }
                else {
                    Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        retrieveData();
    }

}