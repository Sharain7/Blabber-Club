package com.example.chatapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.chatapplication.ChatActivity;
import com.example.chatapplication.R;
import com.example.chatapplication.model.ChatRoomModel;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import org.w3c.dom.Text;
//this is because we want the chat rooms here so use chat room model

public class RecentChatRecyclerAdapter extends FirestoreRecyclerAdapter<ChatRoomModel, RecentChatRecyclerAdapter.ChatRoomModelViewHolder> {
    Context context;

    public RecentChatRecyclerAdapter(@NonNull FirestoreRecyclerOptions<ChatRoomModel> options, Context context) {
        super(options);
        this.context = context;
    }


    @NonNull
    @Override
    public ChatRoomModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.recent_chat_recycler_row, parent, false);
        return new ChatRoomModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ChatRoomModelViewHolder holder, int position, @NonNull ChatRoomModel model) {
        FirebaseUtil.getOtherUserFromChatRoom(model.getUserIds())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            boolean lastMessageSenderMe = model.getLastMessageSenderId().equals(FirebaseUtil.currentUserId());

                            UserModel otherUserModel = task.getResult().toObject(UserModel.class);
                            FirebaseUtil.getCurrentProfileOtherUserReference(otherUserModel.getUserId()).getDownloadUrl()
                                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Uri> task) {
                                            if (task.isSuccessful()) {
                                                Uri uri = task.getResult();
                                                AndroidUtil.setProfilePicture(context, uri, holder.profilePicture);
                                            }
                                        }
                                    });
                            if (otherUserModel != null) {
                                holder.userNameTv.setText(otherUserModel.getUsername());
                                if (lastMessageSenderMe) {
                                    holder.lastMessageTv.setText("You :" + model.getLastMessage());
                                } else {
                                    holder.lastMessageTv.setText(model.getLastMessage());
                                }
                                holder.lastMessageTime.setText(FirebaseUtil.timestampToString(model.getLastMessageTimestamp()));
                                holder.itemView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //move to the chat activity
                                        Intent intent = new Intent(context, ChatActivity.class);
                                        AndroidUtil.passModel(intent, otherUserModel);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(intent);

                                    }
                                });
                            }

                        }
                    }
                });

    }

    public class ChatRoomModelViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTv;
        TextView lastMessageTv;
        ImageView profilePicture;
        TextView lastMessageTime;

        public ChatRoomModelViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            userNameTv = itemView.findViewById(R.id.tv_username);
            lastMessageTime = itemView.findViewById(R.id.last_message_time);
            lastMessageTv = itemView.findViewById(R.id.last_message_text);


        }
    }
}
