package com.example.spotbuddy;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.ColorRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.FragmentManager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.Icon;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class SwipeActivity extends AppCompatActivity implements LocationListener {

    String uid_txt, pin_txt, city_txt, state_txt, country_txt;
    Dialog dialogMatch;
    private long pressedTime;

    public static final int REQUEST_LOCATION_CODE = 1010;
    private int LOCATION_PERMISSION_CODE = 1;
    private LocationRequest locationRequest;
    DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe);

        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        matchUsers();
        FragmentManager fragmentManager = getSupportFragmentManager();
        Button profile = findViewById(R.id.swipe_btn_profile);
        Button swipe = findViewById(R.id.swipe_btn_swipe);
        Button chat = findViewById(R.id.swipe_btn_chat);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, ProfileFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                profile.setEnabled(false);
                swipe.setEnabled(true);
                chat.setEnabled(true);
            }
        });

        swipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, SwipeFragment.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("name") // name can be null
                        .commit();
                profile.setEnabled(true);
                swipe.setEnabled(false);
                chat.setEnabled(true);

            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
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

        checkLocation();
        checkInternet();

    }

    private void checkInternet() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        if (networkInfo == null || !networkInfo.isConnected()) {
            // Internet is not available, prompt the user to enable it
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Internet is not available, please enable it in settings.");
            builder.setCancelable(false);
            builder.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    Intent intent = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(intent);
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    private void checkLocation() {

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // permission is granted, request for location updates
            try {
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        } else {
            // permission is not granted, request for permission
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_CODE);
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext()).checkLocationSettings(builder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    if (ActivityCompat.checkSelfPermission(SwipeActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SwipeActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 500, 5, SwipeActivity.this);
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(SwipeActivity.this, REQUEST_LOCATION_CODE);
                            } catch (IntentSender.SendIntentException ex) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            break;
                    }
                }
            }
        });

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            pin_txt = addresses.get(0).getPostalCode().trim();
            city_txt = addresses.get(0).getLocality().trim();
            state_txt = addresses.get(0).getAdminArea().trim();
            country_txt = addresses.get(0).getCountryName().trim();

            reference.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    reference.child("Users").child(uid_txt).child("PIN").setValue(pin_txt);
                    reference.child("Users").child(uid_txt).child("City").setValue(city_txt);
                    reference.child("Users").child(uid_txt).child("State").setValue(state_txt);
                    reference.child("Users").child(uid_txt).child("Country").setValue(country_txt);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    private void matchUsers() {
        reference.child("Users").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    if (userSnapshot.child("Matches").hasChild(uid_txt) && !(snapshot.child(uid_txt).child("Notified").hasChild(Objects.requireNonNull(userSnapshot.getKey())))) {
                        String user_photo = Objects.requireNonNull(snapshot.child(uid_txt).child("Photo1").getValue()).toString().trim();
                        String match_name = Objects.requireNonNull(snapshot.child(Objects.requireNonNull(userSnapshot.getKey())).child("Name").getValue()).toString().trim();
                        String match_photo = Objects.requireNonNull(snapshot.child(Objects.requireNonNull(userSnapshot.getKey())).child("Photo1").getValue()).toString().trim();
                        reference.child("Users").child(uid_txt).child("Notified").child(userSnapshot.getKey()).setValue(ServerValue.TIMESTAMP);

                        dialogMatch = new Dialog(SwipeActivity.this);
                        dialogMatch.setContentView(R.layout.dialog_match);
                        dialogMatch.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                        dialogMatch.getWindow().setBackgroundDrawableResource(R.color.black);
                        dialogMatch.setCancelable(false);
                        dialogMatch.getWindow().getAttributes().windowAnimations = R.style.matchDialogAnimation;

                        MediaPlayer mediaPlayer = MediaPlayer.create(SwipeActivity.this, R.raw.match);
                        mediaPlayer.start();
                        dialogMatch.show();

                        CircleImageView user = dialogMatch.getWindow().findViewById(R.id.match_img_user);
                        CircleImageView match = dialogMatch.getWindow().findViewById(R.id.match_img_match);
                        TextView liked = dialogMatch.getWindow().findViewById(R.id.match_txt_liked);

                        liked.setText(String.format("You and %s liked each other", match_name));
                        Picasso.get().load(user_photo).into(user);
                        Picasso.get().load(match_photo).into(match);

                        Button profile = findViewById(R.id.swipe_btn_profile);
                        Button swipe = findViewById(R.id.swipe_btn_swipe);
                        Button chat = findViewById(R.id.swipe_btn_chat);
                        Button chating = dialogMatch.findViewById(R.id.match_btn_chat);
                        Button spotbuddy = dialogMatch.findViewById(R.id.match_btn_spotbuddy);
                        ImageButton cancel = dialogMatch.findViewById(R.id.match_ibt_cancel);

                        chating.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                dialogMatch.dismiss();
                                FragmentManager fragmentManager = getSupportFragmentManager();
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
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkLocation();
        checkInternet();
    }

    public void onBackPressed() {

        if (pressedTime + 2000 > System.currentTimeMillis()) {
            super.onBackPressed();
            finish();
        } else {
            Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
        pressedTime = System.currentTimeMillis();
    }
}