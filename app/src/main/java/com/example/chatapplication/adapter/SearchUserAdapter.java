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
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SearchUserAdapter extends FirestoreRecyclerAdapter<UserModel, SearchUserAdapter.UserSearchModelViewHolder> {
    Context context ;

    public SearchUserAdapter(@NonNull FirestoreRecyclerOptions<UserModel> options , Context context) {
        super(options);
        this.context = context ;
    }


    @NonNull
    @Override
    public UserSearchModelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context) ;
        View view = layoutInflater.inflate(R.layout.search_recycler_row , parent,false);
        return new UserSearchModelViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull UserSearchModelViewHolder holder, int position, @NonNull UserModel model) {
        holder.userNameTv.setText(model.getUsername());
        holder.phoneNumberTv.setText(model.getPhoneNumber());
        if(model!=null) {


            if (model.getUserId().equals(FirebaseUtil.currentUserId())) {
                holder.userNameTv.setText(model.getUsername() + " (Me)");
            }
            FirebaseUtil.getCurrentProfileOtherUserReference(model.getUserId()).getDownloadUrl()
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri uri = task.getResult();
                                AndroidUtil.setProfilePicture(context, uri, holder.profilePicture);
                            }
                        }
                    });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //move to the chat activity
                    Intent intent = new Intent(context, ChatActivity.class);
                    AndroidUtil.passModel(intent, model);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }
            });
        }


    }

    public class UserSearchModelViewHolder extends RecyclerView.ViewHolder {
        TextView userNameTv;
        TextView phoneNumberTv;
        ImageView profilePicture;

        public UserSearchModelViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePicture = itemView.findViewById(R.id.profile_picture);
            userNameTv = itemView.findViewById(R.id.tv_username);
            phoneNumberTv  =itemView.findViewById(R.id.tv_phoneNumber);

        }
    }
}
