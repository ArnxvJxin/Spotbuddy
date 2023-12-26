package com.example.spotbuddy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class CardStackAdapter extends PagerAdapter {

    Context context;
    List<ItemModel> usersList ;
    LayoutInflater inflater;
    String age_txt;

    public CardStackAdapter(Context context, List<ItemModel> usersList) {
        this.context = context;
        this.usersList = usersList;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public int getCount() {
        return usersList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @SuppressLint("NewApi")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.item_card, container, false);

        TextView location = view.findViewById(R.id.card_txt_location);
        TextView name = view.findViewById(R.id.card_txt_name);
        TextView profession = view.findViewById(R.id.card_txt_profession);
        TextView college = view.findViewById(R.id.card_txt_college);
        TextView school = view.findViewById(R.id.card_txt_school);
        TextView mbti = view.findViewById(R.id.card_txt_mbtitxt);
        TextView bio = view.findViewById(R.id.card_txt_biotxt);
        TextView interests = view.findViewById(R.id.card_txt_intereststxt);
        ImageView photo1 = view.findViewById(R.id.card_img_one);
        ImageView photo2 = view.findViewById(R.id.card_img_two);
        ImageView photo3 = view.findViewById(R.id.card_img_three);
        ImageView photo4 = view.findViewById(R.id.card_img_four);

        String day_txt = usersList.get(position).getBirthday();
        String month_txt = usersList.get(position).getBirthmonth();
        String year_txt = usersList.get(position).getBirthyear();

        if (day_txt != null || month_txt != null || year_txt != null) {
            int age = 0;
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
            age_txt = Integer.toString(age);
        }

        if (usersList.get(position).getCity() !=null || usersList.get(position).getState() !=null ) {
            location.setText(String.format("%s, %s", usersList.get(position).getCity(), usersList.get(position).getState()));

        } else {
            location.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getName() !=null) {
            name.setText(String.format("%s, %s", usersList.get(position).getName(), age_txt));
        } else {
            name.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getProfession() !=null) {
            profession.setText(usersList.get(position).getProfession());
        } else {
            profession.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getCollege() !=null) {
            college.setText(usersList.get(position).getCollege());
        } else {
            college.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getSchool() !=null) {
            school.setText(usersList.get(position).getSchool());
        } else {
            school.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getMBTI() !=null) {
            mbti.setText(usersList.get(position).getMBTI());
        } else {
            mbti.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getBio() !=null) {
            bio.setText(usersList.get(position).getBio());
        } else {
            bio.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getInterests() !=null) {
            interests.setText(usersList.get(position).getInterests());
        } else {
            interests.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getPhoto1() !=null) {
            Picasso.get().load(usersList.get(position).getPhoto1()).into(photo1);
        } else {
            photo1.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getPhoto2() !=null) {
            Picasso.get().load(usersList.get(position).getPhoto2()).into(photo2);
        } else {
            photo2.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getPhoto3() !=null) {
            Picasso.get().load(usersList.get(position).getPhoto3()).into(photo3);
        } else {
            photo3.setVisibility(View.INVISIBLE);
        }

        if (usersList.get(position).getPhoto4() !=null) {
            Picasso.get().load(usersList.get(position).getPhoto4()).into(photo4);
        } else {
            photo4.setVisibility(View.INVISIBLE);
        }

        container.addView(view);
        return view;
    }
}
