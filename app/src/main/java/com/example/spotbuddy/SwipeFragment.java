package com.example.spotbuddy;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwipeFragment extends Fragment implements IFirebaseSwipeLoadDone, ValueEventListener {
    private CustomViewPager viewPager;
    String uid_txt, preference_txt, agestart_txt, ageend_txt, distance_txt, pin_txt, city_txt, state_txt, country_txt;
    String user_photo, match_photo, match_name, conversationId, promptId, prompt;
    private CardStackAdapter adapter;
    Dialog dialogMatch;
    private DatabaseReference databaseReference;

    ImageButton right, left;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_swipe, container, false);

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        right = view.findViewById(R.id.swipe_fra_btn_right);
        left = view.findViewById(R.id.swipe_fra_btn_left);
        viewPager = view.findViewById(R.id.swipe_fra_viewpager_card);
        viewPager.setOffscreenPageLimit(3);
        viewPager.setSwipeEnabled(false);
        iFirebaseLoadDone = this;
        loadUsers();

        return view;
    }

    IFirebaseSwipeLoadDone iFirebaseLoadDone;

    private void loadUsers() {

        databaseReference.addValueEventListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        List < ItemModel > userList = new ArrayList < > ();

        preference_txt = String.valueOf(snapshot.child(uid_txt).child("Preference").getValue()).trim();
        agestart_txt = String.valueOf(snapshot.child(uid_txt).child("Age Start").getValue()).trim();
        ageend_txt = String.valueOf(snapshot.child(uid_txt).child("Age End").getValue()).trim();
        distance_txt = String.valueOf(snapshot.child(uid_txt).child("Distance").getValue()).trim();
        pin_txt = String.valueOf(snapshot.child(uid_txt).child("PIN").getValue()).trim();
        city_txt = String.valueOf(snapshot.child(uid_txt).child("City").getValue()).trim();
        state_txt = String.valueOf(snapshot.child(uid_txt).child("State").getValue()).trim();
        country_txt = String.valueOf(snapshot.child(uid_txt).child("Country").getValue()).trim();
        user_photo = String.valueOf(snapshot.child(uid_txt).child("Photo1").getValue()).trim();

        for (DataSnapshot userSnapshot: snapshot.getChildren()) {

            String birthday = Objects.requireNonNull(userSnapshot.child("Birthday").getValue()).toString();
            String birthmonth = Objects.requireNonNull(userSnapshot.child("Birthmonth").getValue()).toString();
            String birthyear = Objects.requireNonNull(userSnapshot.child("Birthyear").getValue()).toString();
            String gender = Objects.requireNonNull(userSnapshot.child("Gender").getValue()).toString();
            String pin = Objects.requireNonNull(userSnapshot.child("PIN").getValue()).toString();
            String city = Objects.requireNonNull(userSnapshot.child("City").getValue()).toString();
            String state = Objects.requireNonNull(userSnapshot.child("State").getValue()).toString();
            String country = Objects.requireNonNull(userSnapshot.child("Country").getValue()).toString();
            String s = birthday.trim() + birthmonth.trim() + birthyear.trim();

            String uid = userSnapshot.getKey();
            assert uid != null;
            if (!(snapshot.child(uid_txt).child("Status").getValue() == "Invisible")) {
                if (!(userSnapshot.child("Status").getValue() == "Deleted") || !(userSnapshot.child("Status").getValue() == "Invisible")) {
                    if (!snapshot.child(uid_txt).child("Swipes").hasChild(uid)) {
                        if (Objects.equals(distance_txt, "Area")) {
                            if (Objects.equals(pin_txt, pin)) {
                                if (Objects.equals(preference_txt, "Men")) {
                                    if (Objects.equals("Male", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Women")) {
                                    if (Objects.equals("Female", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Everyone")) {
                                    int age = 0;
                                    try {
                                        LocalDate today = LocalDate.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                        LocalDate birthdate = LocalDate.parse(s, formatter);
                                        age = Period.between(birthdate, today).getYears();
                                    } catch (DateTimeParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                        userList.add(userSnapshot.getValue(ItemModel.class));
                                    }
                                }
                            }
                        } else if (Objects.equals(distance_txt, "City")) {
                            if (Objects.equals(city_txt, city)) {
                                if (Objects.equals(preference_txt, "Men")) {
                                    if (Objects.equals("Male", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Women")) {
                                    if (Objects.equals("Female", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Everyone")) {
                                    int age = 0;
                                    try {
                                        LocalDate today = LocalDate.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                        LocalDate birthdate = LocalDate.parse(s, formatter);
                                        age = Period.between(birthdate, today).getYears();
                                    } catch (DateTimeParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                        userList.add(userSnapshot.getValue(ItemModel.class));
                                    }
                                }
                            }
                        } else if (Objects.equals(distance_txt, "State")) {
                            if (Objects.equals(state_txt, state)) {
                                if (Objects.equals(preference_txt, "Men")) {
                                    if (Objects.equals("Male", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Women")) {
                                    if (Objects.equals("Female", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Everyone")) {
                                    int age = 0;
                                    try {
                                        LocalDate today = LocalDate.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                        LocalDate birthdate = LocalDate.parse(s, formatter);
                                        age = Period.between(birthdate, today).getYears();
                                    } catch (DateTimeParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                        userList.add(userSnapshot.getValue(ItemModel.class));
                                    }
                                }
                            }
                        } else if (Objects.equals(distance_txt, "Country")) {
                            if (Objects.equals(country_txt, country)) {
                                if (Objects.equals(preference_txt, "Men")) {
                                    if (Objects.equals("Male", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Women")) {
                                    if (Objects.equals("Female", gender)) {
                                        int age = 0;
                                        try {
                                            LocalDate today = LocalDate.now();
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                            LocalDate birthdate = LocalDate.parse(s, formatter);
                                            age = Period.between(birthdate, today).getYears();
                                        } catch (DateTimeParseException e) {
                                            e.printStackTrace();
                                        }

                                        if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                            userList.add(userSnapshot.getValue(ItemModel.class));
                                        }
                                    }
                                } else if (Objects.equals(preference_txt, "Everyone")) {
                                    int age = 0;
                                    try {
                                        LocalDate today = LocalDate.now();
                                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyyy");
                                        LocalDate birthdate = LocalDate.parse(s, formatter);
                                        age = Period.between(birthdate, today).getYears();
                                    } catch (DateTimeParseException e) {
                                        e.printStackTrace();
                                    }

                                    if (age >= Integer.parseInt(agestart_txt) && age <= Integer.parseInt(ageend_txt)) {
                                        userList.add(userSnapshot.getValue(ItemModel.class));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            iFirebaseLoadDone.onFirebaseLoadSuccess(userList);
        }
    }

    @Override
    public void onCancelled(@NonNull DatabaseError error) {
        iFirebaseLoadDone.onFirebaseLoadFailed(error.getMessage());
    }

    @Override
    public void onDestroy() {
        databaseReference.removeEventListener(this);
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseReference.addValueEventListener(this);
    }

    @Override
    public void onStop() {
        databaseReference.removeEventListener(this);
        super.onStop();
    }

    @Override
    public void onFirebaseLoadSuccess(List < ItemModel > userList) {
        Collections.shuffle(userList);
        adapter = new CardStackAdapter(getContext(), userList);
        viewPager.setAdapter(adapter);
        left.setEnabled(true);
        right.setEnabled(true);

        if (userList.isEmpty()) {
            viewPager.removeAllViews();
            viewPager.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.caught_up));
            left.setEnabled(false);
            right.setEnabled(false);
        }

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View currentItemLayout = viewPager.getChildAt(viewPager.getCurrentItem());
                ImageView imageView = currentItemLayout.findViewById(R.id.card_img_thumbup);
                imageView.setVisibility(View.VISIBLE);

                int currentItem = viewPager.getCurrentItem();
                int totalItems = Objects.requireNonNull(viewPager.getAdapter()).getCount();
                match_name = userList.get(currentItem).getName();
                match_photo = userList.get(currentItem).getPhoto1();
                String swipe_uid = userList.get(currentItem).getUID();

                if (currentItem == totalItems - 1) {
                    viewPager.removeAllViews();
                    viewPager.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.caught_up));
                    left.setEnabled(false);
                    right.setEnabled(false);
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }

                databaseReference.child(uid_txt).child("Swipes").child(swipe_uid).setValue("1");
                databaseReference.child(swipe_uid).child("Swipes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.hasChild(uid_txt)) {
                            String swipe = snapshot.child(uid_txt).getValue(String.class);
                            assert swipe != null;
                            if (swipe.equals(String.valueOf(1))) {
                                databaseReference.child(uid_txt).child("Matches").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (!snapshot.hasChild(swipe_uid)) {
                                            promptGenerator();
                                            conversationId = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
                                            promptId = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US).format(new Date());
                                            databaseReference.child(uid_txt).child("Matches").child(swipe_uid).setValue(conversationId);
                                            databaseReference.child(swipe_uid).child("Matches").child(uid_txt).setValue(conversationId);
                                            FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId).child("User1").setValue(uid_txt);
                                            FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId).child("User2").setValue(swipe_uid);
                                            FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId).child("Chat").child(conversationId).child("Message").setValue(prompt);
                                            FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId).child("Chat").child(conversationId).child("Sender").setValue("Spotbuddy");
                                            FirebaseDatabase.getInstance().getReference("Conversations").child(conversationId).child("Chat").child(conversationId).child("Timestamp").setValue(promptId);

                                            
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View currentItemLayout = viewPager.getChildAt(viewPager.getCurrentItem());
                ImageView imageView = currentItemLayout.findViewById(R.id.card_img_thumbdown);
                imageView.setVisibility(View.VISIBLE);

                int currentItem = viewPager.getCurrentItem();
                int totalItems = Objects.requireNonNull(viewPager.getAdapter()).getCount();
                String swipe_uid = userList.get(currentItem).getUID();

                if (currentItem == totalItems - 1) {
                    viewPager.removeAllViews();
                    viewPager.setBackground(ContextCompat.getDrawable(requireContext(), R.drawable.caught_up));
                    left.setEnabled(false);
                    right.setEnabled(false);
                } else {
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                }

                databaseReference.child(uid_txt).child("Swipes").child(swipe_uid).setValue("0");

            }
        });

    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
    }

    private void openMatchDialog() {
        dialogMatch = new Dialog(getActivity());
        dialogMatch.setContentView(R.layout.dialog_match);
        dialogMatch.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialogMatch.getWindow().setBackgroundDrawableResource(R.color.black);
        dialogMatch.setCancelable(false);
        dialogMatch.getWindow().getAttributes().windowAnimations = R.style.matchDialogAnimation;

        MediaPlayer mediaPlayer = MediaPlayer.create(getContext(), R.raw.match);
        mediaPlayer.start();
        dialogMatch.show();

        CircleImageView user = dialogMatch.getWindow().findViewById(R.id.match_img_user);
        CircleImageView match = dialogMatch.getWindow().findViewById(R.id.match_img_match);
        TextView liked = dialogMatch.getWindow().findViewById(R.id.match_txt_liked);

        liked.setText(String.format("You and %s liked each other", match_name));
        Picasso.get().load(user_photo).into(user);
        Picasso.get().load(match_photo).into(match);

        Button profile = getActivity().findViewById(R.id.swipe_btn_profile);
        Button swipe = getActivity().findViewById(R.id.swipe_btn_swipe);
        Button chat = getActivity().findViewById(R.id.swipe_btn_chat);
        Button chating = dialogMatch.findViewById(R.id.match_btn_chat);
        Button spotbuddy = dialogMatch.findViewById(R.id.match_btn_spotbuddy);
        ImageButton cancel = dialogMatch.findViewById(R.id.match_ibt_cancel);

        chating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMatch.dismiss();
                FragmentManager fragmentManager = getParentFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ChatFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                profile.setEnabled(true);
                swipe.setEnabled(true);
                chat.setEnabled(false);
            }
        });

        spotbuddy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMatch.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogMatch.dismiss();
            }
        });
    }

    private void promptGenerator() {
        String[] questions = {
                "What is your current passion project?",
                "Do you have any hobbies?",
                "How do you like to spend your free time?",
                "What kind of music are you into?",
                "Do you have any pets?",
                "What’s your claim to fame?",
                "Who is one of your favorite celebrity role models?",
                "What is your dream job?",
                "What is at the top of your bucket list?",
                "If you could travel anywhere in the world, where would you go?",
                "What skill would you like to master?",
                "What do you like to learn about?",
                "What has been the highlight of this past week?",
                "What has been the highlight of your year?",
                "Have you read any intriguing books lately?",
                "What’s your current favorite TV show/movie?",
                "Do you have any nicknames?",
                "What is your favorite place you’ve traveled to?",
                "Where did you grow up?",
                "How long have you lived in [city]?",
                "What is your favorite food?",
                "What is your favorite sport?",
                "Where is your favorite restaurant around here?",
                "How do you take your coffee or tea?",
                "If you could be any type of dog, what breed would you be and why?",
                "What is something you’ve always wanted to learn?",
                "What is your biggest regret?",
                "If you could trade places with anyone for a week, who would it be?",
                "What fictional character do you most relate to?",
                "What do you daydream about?"
        };
        int randomIndex = (int)(Math.random() * questions.length);
        prompt = questions[randomIndex];
    }
}