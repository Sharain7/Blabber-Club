package com.example.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.R;
import com.example.chatapplication.model.ChatMessageModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class ChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, ChatRecyclerAdapter.ChatModelViewHolder> {
    Context context ;

    public ChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options , Context context) {
        super(options);
        this.context = context ;
    }


    @NonNull
    @Override
    public ChatModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context) ;
        View view = layoutInflater.inflate(R.layout.chat_message_rv_row , parent,false);
        return new ChatModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatModelViewHolder holder, int position, @NonNull ChatMessageModel model) {
        if(model.getSenderId().equals(FirebaseUtil.currentUserId())){
            //this means it's me that is the current user
            holder.leftSideChatLayout.setVisibility(View.GONE);
            holder.rightSideChatLayout.setVisibility(View.VISIBLE);
            holder.rightSideTextView.setText(model.getMessage());


        }
        else{
            //sent by the other user
            holder.leftSideChatLayout.setVisibility(View.VISIBLE);
            holder.rightSideChatLayout.setVisibility(View.GONE);
            holder.leftSideTextView.setText(model.getMessage());
        }



    }

    public class ChatModelViewHolder extends RecyclerView.ViewHolder {

        LinearLayout leftSideChatLayout , rightSideChatLayout;
        TextView leftSideTextView ,rightSideTextView;

        public ChatModelViewHolder(@NonNull View itemView) {
            super(itemView);
            leftSideChatLayout = itemView.findViewById(R.id.ll_left_side);
            rightSideChatLayout = itemView.findViewById(R.id.ll_right_side);
            leftSideTextView = itemView.findViewById(R.id.left_side_chat_tv);
            rightSideTextView  = itemView.findViewById(R.id.right_side_chat_tv);



        }
    }
}
