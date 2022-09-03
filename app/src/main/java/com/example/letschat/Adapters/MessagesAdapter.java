package com.example.letschat.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.letschat.Models.Message;
import com.example.letschat.R;
import com.example.letschat.databinding.ItemRecieveBinding;
import com.example.letschat.databinding.ItemSendBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter{
    Context context ;
    ArrayList<Message> messages;
    final int ITEM_SENT =1;
    final int ITEM_RECIEVE =2;

    public MessagesAdapter(Context context, ArrayList<Message> messages) {
        this.context = context;
        this.messages = messages;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==ITEM_SENT)
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_send,parent,false);
            return  new SendViewHolder(view);

        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_recieve,parent,false);
            return  new RecieveViewHolder(view);

        }



    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderId()))
        {
            return  ITEM_SENT;
        }
        else
        {
            return ITEM_RECIEVE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);
        if(holder.getClass()==SendViewHolder.class){
            SendViewHolder viewHolder = (SendViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());
        }
        else
        {
            RecieveViewHolder viewHolder =(RecieveViewHolder) holder;
            viewHolder.binding.message.setText(message.getMessage());

        }

    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    class SendViewHolder extends RecyclerView.ViewHolder{
         ItemSendBinding binding;

         public SendViewHolder(@NonNull View itemView) {
             super(itemView);
             binding = ItemSendBinding.bind(itemView);
         }
     }


    class RecieveViewHolder extends RecyclerView.ViewHolder{
      ItemRecieveBinding binding;

        public RecieveViewHolder(@NonNull View itemView) {
            super(itemView);
            binding=ItemRecieveBinding.bind(itemView);
        }
    }


}
