package com.example.letschat.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Activities.ChatActivity;
import com.example.letschat.Models.User;
import com.example.letschat.R;
import com.example.letschat.databinding.RowConversationBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyviewHolder>{
    Context context;
    ArrayList<User> userlist;
    FirebaseAuth auth = FirebaseAuth.getInstance();

    public UserAdapter(Context context, ArrayList<User> userlist) {
        this.context = context;
        this.userlist = userlist;
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_conversation,parent,false);
        return new MyviewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder holder, int position) {

        User user =userlist.get(position);

            holder.binding.userName.setText(user.getName());
            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.avatar).into(holder.binding.profileImage);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("name",user.getName());
                    intent.putExtra("uid",user.getuId());
                    context.startActivity(intent);
                }
            });






    }

    @Override
    public int getItemCount() {
        return userlist.size();
    }

    class MyviewHolder extends RecyclerView.ViewHolder {
        RowConversationBinding binding;
        public MyviewHolder(@NonNull View itemView) {
            super(itemView);
            binding = RowConversationBinding.bind(itemView);
        }
    }
}
