package com.example.letschat.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.letschat.Adapters.StatusAdapter;
import com.example.letschat.Adapters.UserAdapter;
import com.example.letschat.Models.Status;
import com.example.letschat.Models.User;
import com.example.letschat.Models.UserStatus;
import com.example.letschat.R;
import com.example.letschat.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    UserAdapter userAdapter;
    ArrayList<User> userArrayList;
    FirebaseDatabase firebaseDatabase;
    ActivityMainBinding binding;
    FirebaseAuth auth;
    StatusAdapter statusAdapter;
    ArrayList<UserStatus> userStatuses;
    ProgressDialog dialog;
    User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        auth = FirebaseAuth.getInstance();
        dialog=new ProgressDialog(this);
        dialog.setMessage("Uploading Image");
        dialog.setCancelable(false);

        setContentView(binding.getRoot());

        binding.navBar.setVisibility(View.INVISIBLE);



        userStatuses = new ArrayList<>();
        statusAdapter = new StatusAdapter(MainActivity.this,userStatuses);
        binding.statusRecyler.setAdapter(statusAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        binding.statusRecyler.setLayoutManager(layoutManager);
        firebaseDatabase = FirebaseDatabase.getInstance();
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this,userArrayList);



        binding.userRecyler.setAdapter(userAdapter);
        binding.userRecyler.setLayoutManager(new LinearLayoutManager(this));
        binding.userRecyler.showShimmerAdapter();
        binding.statusRecyler.showShimmerAdapter();



        firebaseDatabase.getReference().child("users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





        binding.navBar.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.status:
                    {
                        Toast.makeText(MainActivity.this, "Select an Image...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent,17);
                        break;
                    }
                }
                return false;
            }
        });

        firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);

                   if(!auth.getUid().equals(user.getuId()))
                   {
                       userArrayList.add(user);
                   }




                }
                binding.userRecyler.hideShimmerAdapter();
                binding.navBar.setVisibility(View.VISIBLE);
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        firebaseDatabase.getReference().child("stories")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists())
                        {
                            userStatuses.clear();
                            for(DataSnapshot storySnapshot: snapshot.getChildren())
                            {
                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(storySnapshot.child("name").getValue(String.class));
                                userStatus.setProfileImg(storySnapshot.child("profileImage").getValue(String.class));
                                userStatus.setLastUpdated(storySnapshot.child("lastUpdated").getValue(Long.class));
                                userStatuses.add(userStatus);
                                ArrayList<Status> statuses = new ArrayList<>();
                                for(DataSnapshot status: storySnapshot.child("statuses").getChildren())
                                {
                                    Status sampleStatus = status.getValue(Status.class);
                                    statuses.add(sampleStatus);

                                }
                                userStatus.setStatuses(statuses);
                            }
                            binding.statusRecyler.hideShimmerAdapter();
                            statusAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
          switch (item.getItemId())
          {
              case R.id.search:
                  Toast.makeText(this, "search", Toast.LENGTH_SHORT).show();
                   break;
              case R.id.groups:
                  Toast.makeText(this, "groups", Toast.LENGTH_SHORT).show();
                  break;
              case R.id.settings:
                  Toast.makeText(this, "settings", Toast.LENGTH_SHORT).show();
                  break;

              case R.id.invite:
                  Toast.makeText(this, "invite", Toast.LENGTH_SHORT).show();
                  break;


               }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data!=null)
        {
            dialog.show();
            FirebaseStorage storage = FirebaseStorage.getInstance();
            Date date = new Date();
            StorageReference reference = storage.getReference().child("status").child(date.getTime()+ "");
            reference.putFile(data.getData()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String imgURL = uri.toString();

                                UserStatus userStatus = new UserStatus();
                                userStatus.setName(user.getName());
                                userStatus.setProfileImg(user.getProfileImage());
                                userStatus.setLastUpdated(date.getTime());

                                HashMap<String,Object> obj = new HashMap<>();
                                obj.put("name",userStatus.getName());
                                obj.put("profileImage",userStatus.getProfileImg());
                                obj.put("lastUpdated",userStatus.getLastUpdated());
                                Status status = new Status(imgURL,userStatus.getLastUpdated());

                                firebaseDatabase.getReference().child("stories")
                                                .child(auth.getUid())
                                                        .updateChildren(obj);

                                firebaseDatabase.getReference().child("stories")
                                                .child(auth.getUid())
                                                        .child("statuses")
                                                                .push()
                                                                        .setValue(status);

                                dialog.dismiss();


                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseDatabase.getReference().child("presence")
                .child(auth.getUid())
                .setValue("Online");
    }

//    @Override
//    protected void onStop() {
//        super.onStop();
//        firebaseDatabase.getReference().child("presence")
//                .child(auth.getUid())
//                .setValue("Offline");
//    }


    @Override
    protected void onPause() {
        super.onPause();
        firebaseDatabase.getReference().child("presence")
               .child(auth.getUid())
              .setValue("Offline");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Distroyed!", Toast.LENGTH_SHORT).show();
        firebaseDatabase.getReference().child("presence")
                .child(auth.getUid())
                .setValue("Offline");
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}