package com.example.chatapplication;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatapplication.adapter.RecentChatRecyclerAdapter;
import com.example.chatapplication.adapter.SearchUserAdapter;
import com.example.chatapplication.model.ChatRoomModel;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class ChatFragment extends Fragment {

    RecyclerView recyclerView ;
    RecentChatRecyclerAdapter recentChatRecyclerAdapter ;


    public ChatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        recyclerView = view.findViewById(R.id.recycler_view_chat_fragment);
        setUpRecyclerView();
        return view ;
    }

    private void setUpRecyclerView() {

        Query query = FirebaseUtil.allChatRoomCollectionReference()
                .whereArrayContains("userIds"  , FirebaseUtil.currentUserId() )
                .orderBy("lastMessageTimestamp" , Query.Direction.DESCENDING) ;

        FirestoreRecyclerOptions<ChatRoomModel> options  = new FirestoreRecyclerOptions.Builder<ChatRoomModel>()
                .setQuery(query,ChatRoomModel.class).build();

        //Get the users from the Firestore and populare in rv
         recentChatRecyclerAdapter = new RecentChatRecyclerAdapter(options,getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(recentChatRecyclerAdapter);
        recentChatRecyclerAdapter.startListening();
        recentChatRecyclerAdapter.notifyDataSetChanged();

    }
}