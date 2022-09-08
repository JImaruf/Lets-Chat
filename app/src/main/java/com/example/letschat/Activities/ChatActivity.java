package com.example.letschat.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.letschat.Adapters.MessagesAdapter;
import com.example.letschat.Models.Message;
import com.example.letschat.R;
import com.example.letschat.databinding.ActivityChatBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class ChatActivity extends AppCompatActivity {
    ActivityChatBinding binding;
    String name,recieveruid;
    MessagesAdapter messagesAdapter;
    ArrayList<Message> messageArrayList;
    String senderUid;
    String senderRoom;
    String receiverRoom;
    FirebaseDatabase database;
    FirebaseStorage storage;
    String profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =
                ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance();


        senderUid = FirebaseAuth.getInstance().getUid();
        name = getIntent().getStringExtra("name");
        profileImage = getIntent().getStringExtra("profileImage");
        recieveruid = getIntent().getStringExtra("uid");
        senderRoom = senderUid+recieveruid;
        receiverRoom =recieveruid+senderUid;
        binding.name.setText(name);
        Picasso.get().load(profileImage).placeholder(R.drawable.avatar).into(binding.profileImageDP);
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        getSupportActionBar().setTitle(name);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);





        messageArrayList = new ArrayList<>();
        messagesAdapter = new MessagesAdapter(ChatActivity.this,messageArrayList,senderRoom,receiverRoom);
        binding.messagecontainer.setLayoutManager(new LinearLayoutManager(this));
        binding.messagecontainer.setAdapter(messagesAdapter);

        database.getReference().child("presence").child(recieveruid)
                        .addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists())
                                {
                                    String status = snapshot.getValue(String.class);
                                    if(!status.isEmpty())
                                    {
                                        binding.activityStatus.setText(status);
                                        binding.activityStatus.setVisibility(View.VISIBLE);
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });



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

        final Handler handler = new Handler();
        binding.msgBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                database.getReference().child("presence")
                        .child(FirebaseAuth.getInstance().getUid())
                        .setValue("Typing...");
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(userstopTyping,1000);



            }
            Runnable userstopTyping = new Runnable() {
                @Override
                public void run() {
                    database.getReference().child("presence")
                            .child(FirebaseAuth.getInstance().getUid())
                            .setValue("Online");

                }
            };
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

        binding.attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChatActivity.this, "Select an Image...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,17);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==17)
        {
            if(data!= null)
            {
                if(data.getData()!=null)
                {
                    Uri selectedImage = data.getData();
                    Calendar calendar = Calendar.getInstance();
                    StorageReference reference = storage.getReference().child("chats")
                            .child(calendar.getTimeInMillis()+"");
                    reference.putFile(selectedImage).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if(task.isSuccessful())
                            {
                                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        String filePath = uri.toString();

                                        Date date = new Date();


                                        String msgText = binding.msgBox.getText().toString();
                                        Message message = new Message(msgText,senderUid,date.getTime());
                                        message.setImageUrl(filePath);
                                        message.setMessage("photo");
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

                                        Toast.makeText(ChatActivity.this, "image save "+filePath, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });



                }
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    protected void onResume() {
        super.onResume();

        database.getReference().child("presence")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue("Online");
    }



//    @Override
//    protected void onStop() {
//        super.onStop();
//        database.getReference().child("presence")
//                .child(FirebaseAuth.getInstance().getUid())
//                .setValue("Offline");
//    }


    @Override
    protected void onPause() {
        super.onPause();
        database.getReference().child("presence")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        database.getReference().child("presence")
                .child(FirebaseAuth.getInstance().getUid())
                .setValue("Offline");
    }
}