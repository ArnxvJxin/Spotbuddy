package com.example.spotbuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements IFirebaseChatLoadDone, ValueEventListener {

    TextView matchName;
    CircleImageView matchPhoto;
    EditText writeMessage;
    String uid_txt, conversationId, messageid, dateandtime_txt, message_txt, match_txt;
    DatabaseReference databaseReference, matchReference;
    private ChatAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        databaseReference = FirebaseDatabase.getInstance().getReference("Conversations");
        matchReference = FirebaseDatabase.getInstance().getReference();
        recyclerView = findViewById(R.id.chat_recyclerview_chat);
        matchName = findViewById(R.id.chat_txt_match);
        matchPhoto = findViewById(R.id.chat_img_match);
        writeMessage = findViewById(R.id.chat_edt_message);

        uid_txt = Objects.requireNonNull(FirebaseAuth.getInstance().getUid()).trim();
        String name_txt = getIntent().getStringExtra("name");
        String photo_txt = getIntent().getStringExtra("photo");
        match_txt = getIntent().getStringExtra("uid");
        conversationId = getIntent().getStringExtra("serverstamp");
        String[] nameParts = name_txt.split(" ");
        String firstName = nameParts[0];

        matchName.setText(firstName);
        Picasso.get().load(photo_txt).into(matchPhoto);

        ImageButton speech = findViewById(R.id.chat_ibt_speech);
        speech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SpeakNow(view);
            }
        });


        ImageButton back = findViewById(R.id.chat_ibt_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ImageButton send = findViewById(R.id.chat_ibt_send);
            send.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                    Date now = new Date();
                    dateandtime_txt = formatter.format(now);
                    message_txt = writeMessage.getText().toString();
                    messageid = new SimpleDateFormat ("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
                    if (!message_txt.isEmpty()) {
                        FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Message").setValue(message_txt);
                        FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Timestamp").setValue(dateandtime_txt);
                        FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Sender").setValue(uid_txt);
                        writeMessage.setText(null);
                    }
                }
            });

        ImageButton unmatch = findViewById(R.id.chat_ibt_unmatch);
        unmatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder unmatchAlert = new AlertDialog.Builder(ChatActivity.this);
                unmatchAlert.setMessage("Do you want to unmatch with this account?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Message").setValue("Match No Longer Exists.");
                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Timestamp").setValue(dateandtime_txt);
                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Sender").setValue("Spotbuddy");
                                FirebaseDatabase.getInstance().getReference().child("Users").child(uid_txt).child("Umatched").child(match_txt).setValue(conversationId);
                                FirebaseDatabase.getInstance().getReference().child("Users").child(match_txt).child("Unmatched").child(uid_txt).setValue(conversationId);
                                Toast.makeText(ChatActivity.this, "You Have Unmatched", Toast.LENGTH_SHORT).show();
                                finish();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                unmatchAlert.create();
                unmatchAlert.show();
            }
        });

        ImageButton report = findViewById(R.id.chat_ibt_report);
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder reportAlert = new AlertDialog.Builder(ChatActivity.this);
                reportAlert.setMessage("Do you want to report this account?").setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                                Date now = new Date();
                                dateandtime_txt = formatter.format(now);
                                messageid = new SimpleDateFormat ("yyyyMMddHHmmssSSS", Locale.US).format(new Date());
                                Log.d("pop",messageid+"$"+uid_txt+"$"+match_txt+"$"+dateandtime_txt);
                                FirebaseDatabase.getInstance().getReference().child("Reports").child(messageid).child("Reporter").setValue(uid_txt);
                                FirebaseDatabase.getInstance().getReference().child("Reports").child(messageid).child("Reported").setValue(match_txt);
                                FirebaseDatabase.getInstance().getReference().child("Reports").child(messageid).child("Time").setValue(dateandtime_txt);
                                FirebaseDatabase.getInstance().getReference().child("Reports").child(messageid).child("Conversation").setValue(conversationId);
                                Toast.makeText(ChatActivity.this, "Account Reported", Toast.LENGTH_SHORT).show();

                                AlertDialog.Builder unmatchAlert = new AlertDialog.Builder(ChatActivity.this);
                                unmatchAlert.setMessage("Do you want to unmatch with this account?").setCancelable(false)
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int which) {
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(uid_txt).child("Umatched").child(match_txt).setValue(conversationId);
                                                FirebaseDatabase.getInstance().getReference().child("Users").child(match_txt).child("Unmatched").child(uid_txt).setValue(conversationId);
                                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Message").setValue("Match No Longer Exists.");
                                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Timestamp").setValue(dateandtime_txt);
                                                FirebaseDatabase.getInstance().getReference().child("Conversations").child(conversationId).child("Chat").child(messageid).child("Sender").setValue("Spotbuddy");
                                                Toast.makeText(ChatActivity.this, "You Have Unmatched", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.cancel();
                                            }
                                        });
                                unmatchAlert.create();
                                unmatchAlert.show();

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                reportAlert.create();
                reportAlert.show();
            }
        });

        matchReference.child("Users").child(uid_txt).child("Umatched").child(match_txt).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    send.setVisibility(View.INVISIBLE);
                    speech.setVisibility(View.INVISIBLE);
                    writeMessage.setVisibility(View.INVISIBLE);
                    unmatch.setVisibility(View.INVISIBLE);
                    report.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        iFirebaseLoadDone = ChatActivity.this;
        loadMessages();

        }

    IFirebaseChatLoadDone iFirebaseLoadDone;
    private void loadMessages() {
        databaseReference.addValueEventListener(this);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onDataChange(@NonNull DataSnapshot snapshot) {
        List<ChatModel> chatList = new ArrayList<>();
        for (DataSnapshot userSnapshot : snapshot.child(conversationId).child("Chat").getChildren()) {
                if (!(userSnapshot.child("Message").getValue() == null) && !(userSnapshot.child("Timestamp").getValue() == null) && !(userSnapshot.child("Sender").getValue() == null)) {
                    String message = Objects.requireNonNull(userSnapshot.child("Message").getValue()).toString();
                    String timestamp = Objects.requireNonNull(userSnapshot.child("Timestamp").getValue()).toString().trim();
                    String sender = Objects.requireNonNull(userSnapshot.child("Sender").getValue()).toString().trim();
                    String chatId = Objects.requireNonNull(userSnapshot.getKey()).trim();
                    if (sender.equals(uid_txt)) {
                        chatList.add(new ChatModel(ChatModel.LAYOUT_SENDER, message, timestamp, sender, chatId));
                    } else if (sender.equals(match_txt)) {
                        chatList.add(new ChatModel(ChatModel.LAYOUT_RECEIVER, message, timestamp, sender, chatId));
                    } else {
                        chatList.add(new ChatModel(ChatModel.LAYOUT_ANNOUNCEMENT, message, timestamp, sender, chatId));
                    }
                }
        }
        if (!(chatList.size() == 0) ) {
            Collections.sort(chatList, new Comparator<ChatModel>() {
                @Override
                public int compare(ChatModel o1, ChatModel o2) {
                    return o1.getChatId().compareToIgnoreCase(o2.getChatId());
                }
            });
        }
        iFirebaseLoadDone.onFirebaseLoadSuccess(chatList);
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
    public void onFirebaseLoadSuccess(List<ChatModel> chatList) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ChatAdapter(this, chatList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        layoutManager.setStackFromEnd(true);
    }

    @Override
    public void onFirebaseLoadFailed(String message) {
        Toast.makeText(this, "" + message, Toast.LENGTH_SHORT).show();
    }

    private void SpeakNow (View view) {
        Intent i = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        i.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        i.putExtra(RecognizerIntent.EXTRA_PROMPT, "Start Speaking...");
        startActivityForResult(i, 111);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 111 && resultCode == RESULT_OK) {
            writeMessage.setText(data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        }
    }
}