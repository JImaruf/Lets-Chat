package com.example.letschat.Activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.letschat.Adapters.MessagesAdapter;
import com.example.letschat.Models.Message;
import com.example.letschat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String name,recieveruid;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messageArrayList;
    String senderUid;
    String senderRoom;
    String receiverRoom;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =
                ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().setTitle(name);
        database = FirebaseDatabase.getInstance();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        senderUid = FirebaseAuth.getInstance().getUid();
        name = getIntent().getStringExtra("name");
        recieveruid = getIntent().getStringExtra("uid");
        senderRoom = senderUid+recieveruid;
        receiverRoom =recieveruid+senderUid;





        messageArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messageArrayList,senderRoom,receiverRoom);
        binding.messagecontainer.setLayoutManager(new LinearLayoutManager(this));
        binding.messagecontainer.setAdapter(messagesAdapter);



        database.getReference().child("chats").child(senderRoom).child("messages")
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                messageArrayList.clear();
                                for(DataSnapshot dataSnapshot: snapshot.getChildren())
                                {
                                    Message message = dataSnapshot.getValue(Message.class);
                                    message.setMessageId(dataSnapshot.getKey());
                                    messageArrayList.add(message);

                                }
                                messagesAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

        binding.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = new Date();
                String msgText = binding.msgBox.getText().toString();
                Message message = new Message(msgText,senderUid,date.getTime());
                binding.msgBox.setText("");

                String randomKey=database.getReference().push().getKey();

                HashMap<String,Object> lastMsgObj = new HashMap<>();
                lastMsgObj.put("lastmsg",message.getMessage());
                lastMsgObj.put("lastmsgTime",date.getTime());

                database.getReference().child("chats").child(senderRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(receiverRoom).updateChildren(lastMsgObj);
                database.getReference().child("chats").child(senderRoom).child("messages")
                        .child(randomKey)
                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                database.getReference().child("chats").child(receiverRoom).child("messages")
                                        .child(randomKey)
                                        .setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {

                                            }
                                        });

                            }
                        });

            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }
}