package com.example.spotbuddy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatFragment extends Fragment implements IFirebaseConversationLoadDone, ValueEventListener {
    private DatabaseReference databaseReference;
    private ConversationAdapter adapter;
    RecyclerView recyclerView;
    String uid_txt, name_txt, photo1_txt, timestamp;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.chat_fra_recyclerview_conversations);
        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid_txt).child("Matches");
        iFirebaseLoadDone = this;
        loadConversations();
        return view;
    }

    IFirebaseConversationLoadDone iFirebaseLoadDone;

    private void loadConversations() {
        databaseReference.addValueEventListener(this);
    }

    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        List<ConversationModel> conversationList = new ArrayList<>();

        for (DataSnapshot userSnapshot : snapshot.getChildren()) {
            String match_uid = Objects.requireNonNull(userSnapshot.getKey()).trim();
            String serverstamp = Objects.requireNonNull(userSnapshot.getValue()).toString().trim();
            FirebaseDatabase.getInstance().getReference("Users").child(match_uid).addValueEventListener(new ValueEventListener() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    name_txt = Objects.requireNonNull(snapshot.child("Name").getValue()).toString();
                    photo1_txt = Objects.requireNonNull(snapshot.child("Photo1").getValue()).toString();

                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.US);
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yy HH:mm", Locale.US);
                    String outputStr = "";

                    try {
                        Date date = inputFormat.parse(serverstamp);
                        assert date != null;
                        timestamp = outputFormat.format(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    conversationList.add(new ConversationModel(match_uid, name_txt, photo1_txt, "You matched with "+name_txt, timestamp, serverstamp));
                    Collections.sort(conversationList, new Comparator<ConversationModel>() {
                        @Override
                        public int compare(ConversationModel o1, ConversationModel o2) {
                            return o2.getServerstamp().compareToIgnoreCase(o1.getServerstamp());
                        }
                    });
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        iFirebaseLoadDone.onFirebaseLoadSuccess(conversationList);
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

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onFirebaseLoadSuccess(List<ConversationModel> conversationList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ConversationAdapter(getContext(), conversationList);
        recyclerView.setAdapter(adapter);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setHasFixedSize(true);
        Log.d("yuck", String.valueOf(adapter.getItemCount()));


    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(getContext(), "" + message, Toast.LENGTH_SHORT).show();
    }
}
