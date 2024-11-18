package com.learnoset.chatapplication.chat;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.learnoset.chatapplication.MemoryData;
import com.learnoset.chatapplication.R;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class Chat extends AppCompatActivity {

    // array list to store chat data
    private final List<ChatList> chatLists = new ArrayList<>();

    // creating database reference
    private DatabaseReference databaseReference;

    private String userMobileNumber = "";
    private String chatKey = "";
    private RecyclerView chattingRecyclerView;
    private ChatAdapter chatAdapter;
    private boolean loadingFirstTime = true;

    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        final ImageView backBtn = findViewById(R.id.backBtn);
        final TextView nameTV = findViewById(R.id.name);
        final EditText messageEditText = findViewById(R.id.messageEditTxt);
        final ImageView sendBtn = findViewById(R.id.sendBtn);

        chattingRecyclerView = findViewById(R.id.chattingRecyclerView);

        // configure recyclerView
        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(Chat.this));

        // getting database reference
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url));

        // getting chat and other user details from intent extra
        final String getName = getIntent().getStringExtra("full_name");
        chatKey = getIntent().getStringExtra("chat_key");
        final String getMobile = getIntent().getStringExtra("mobile");

        // get user's mobile number from memory
        userMobileNumber = MemoryData.getMobile(this);

        // generate chat key if empty
        if (chatKey.isEmpty()) {
            chatKey = userMobileNumber + getMobile;
        }

        // setting other user's full name to TextView
        nameTV.setText(getName);

        // setting adapter to recyclerView
        chatAdapter = new ChatAdapter(chatLists, Chat.this);
        chattingRecyclerView.setAdapter(chatAdapter);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // getting last saved message timestamps from memory
                final long lastSavedTimestamps = Long.parseLong(MemoryData.getLastMsgTS(Chat.this, chatKey));

                // getting all chat messages from chat key
                for (DataSnapshot chatMessages : snapshot.child("chat").child(chatKey).getChildren()) {

                    // check if message and mobile exists in database
                    if (chatMessages.hasChild("msg") && chatMessages.hasChild("mobile")) {

                        // getting message details
                        final long getMessageTimeStamps = Long.parseLong(Objects.requireNonNull(chatMessages.getKey()));

                        // if loading first time then load all messages else load only latest messages
                        if (loadingFirstTime || getMessageTimeStamps > lastSavedTimestamps) {

                            // getting chat messages
                            final String getUserMobile = chatMessages.child("mobile").getValue(String.class);
                            final String getMsg = chatMessages.child("msg").getValue(String.class);

                            final String messageDate = generateDateFromTimestamps(getMessageTimeStamps, "dd-MM-yyyy");
                            final String messageTime = generateDateFromTimestamps(getMessageTimeStamps, "hh:mm aa");

                            // chat list
                            final ChatList chatList = new ChatList(getUserMobile, getMsg, messageDate, messageTime);
                            chatLists.add(chatList);

                            // updating chat list
                            chatAdapter.updateChatList(chatLists);
                            chattingRecyclerView.scrollToPosition(chatLists.size() - 1); // scroll to last message in the array list

                            // saving timestamps to memory
                            MemoryData.saveLastMsgTS(String.valueOf(getMessageTimeStamps), chatKey, Chat.this);

                        }
                    }
                }

                // data has loaded first time so assign the variable as false
                loadingFirstTime = false;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chat.this, "Database Error", Toast.LENGTH_SHORT).show();
            }
        };

        // getting chat from firebase realtime database
        databaseReference.addValueEventListener(valueEventListener);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String getTxtMessage = messageEditText.getText().toString();

                if (!getTxtMessage.isEmpty()) {

                    // get current timestamps
                    final String currentTimestamp = String.valueOf(System.currentTimeMillis());

                    databaseReference.child("chat").child(chatKey).child(currentTimestamp).child("msg").setValue(getTxtMessage);
                    databaseReference.child("chat").child(chatKey).child(currentTimestamp).child("mobile").setValue(userMobileNumber);

                    // clear edit text
                    messageEditText.setText("");
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseReference.removeEventListener(valueEventListener);
                finish();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                databaseReference.removeEventListener(valueEventListener);
                finish();
            }
        });
    }

    private String generateDateFromTimestamps(long timestamps, String format) {

        Timestamp ts = new Timestamp(timestamps);
        Date date = new Date(ts.getTime());
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }
}