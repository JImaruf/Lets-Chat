package com.example.letschat.Activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.letschat.Adapters.UserAdapter;
import com.example.letschat.Models.User;
import com.example.letschat.R;
import com.example.letschat.databinding.ActivityMainBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    UserAdapter userAdapter;
    ArrayList<User> userArrayList;
    FirebaseDatabase firebaseDatabase;
    ActivityMainBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        firebaseDatabase = FirebaseDatabase.getInstance();
        userArrayList = new ArrayList<>();
        userAdapter = new UserAdapter(MainActivity.this,userArrayList);

        binding.userRecyler.setAdapter(userAdapter);
        binding.userRecyler.setLayoutManager(new LinearLayoutManager(this));

        firebaseDatabase.getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userArrayList.clear();
                for(DataSnapshot snapshot1: snapshot.getChildren())
                {
                    User user = snapshot1.getValue(User.class);
                    userArrayList.add(user);

                }
                userAdapter.notifyDataSetChanged();
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
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.optionmenu,menu);
        return super.onCreateOptionsMenu(menu);
    }
}