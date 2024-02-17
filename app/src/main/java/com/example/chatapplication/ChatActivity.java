package com.example.chatapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.chatapplication.adapter.ChatRecyclerAdapter;

import com.example.chatapplication.model.ChatMessageModel;
import com.example.chatapplication.model.ChatRoomModel;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.AndroidUtil;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatActivity extends AppCompatActivity {

    UserModel otherUser ;
    String chatRoomId;
    EditText msgInput ;
    ImageView sendBtn , backBtn ;
    TextView otherUserNameTextView ;
    RecyclerView recyclerView ;
    ChatRoomModel chatRoomModel ;
    ChatMessageModel chatMessageModel ;
    ChatRecyclerAdapter chatRecyclerAdapter;
    ImageView profilePicture;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initUI();
    }

    private void initUI() {
        //get the user model
        otherUser = AndroidUtil.getUserModelFromIntent(getIntent());
        chatRoomId = FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(), otherUser.getUserId()); // Assign chatRoomId

        //FirebaseUtil.getChatRoomId(FirebaseUtil.currentUserId(),otherUser.getUserId());
        profilePicture = findViewById(R.id.profile_picture);
        msgInput = findViewById(R.id.edtxEnterMessage);
        sendBtn  = findViewById(R.id.imv_send);
        backBtn = findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.chat_rv);
        otherUserNameTextView = findViewById(R.id.other_user_tv);
        FirebaseUtil.getCurrentProfileOtherUserReference(otherUser.getUserId()).getDownloadUrl()
                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            Uri uri = task.getResult();
                            AndroidUtil.setProfilePicture(ChatActivity.this, uri, profilePicture );
                        }
                    }
                });
       backBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               onBackPressed();
           }
       });
       sendBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               handleSendButton();
           }
       });
       otherUserNameTextView.setText(otherUser.getUsername());
       getOrSetChatRoom();
       setUpChatRecyclerView();

    }

    private void setUpChatRecyclerView() {
        Query query = FirebaseUtil.getChatRoomMessageReference(chatRoomId)
              .orderBy("timestamp" , Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<ChatMessageModel> options  = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query,ChatMessageModel.class).build();

        //Get the users from the Firestore and populare in rv
        chatRecyclerAdapter = new ChatRecyclerAdapter(options,  getApplicationContext());
        LinearLayoutManager  linearLayoutManager  =new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatRecyclerAdapter);
        chatRecyclerAdapter.startListening();
        chatRecyclerAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                recyclerView.smoothScrollToPosition(0);
            }
        });
    }

    private void handleSendButton() {
        String message = msgInput.getText().toString().trim();
        if(message.isEmpty()){
            return;
        }
        sendMessageToUser(message);
    }

    private void sendMessageToUser(String chatMessage) {
        chatRoomModel.setLastMessage(chatMessage);
        chatRoomModel.setLastMessageTimestamp(Timestamp.now());
        chatRoomModel.setLastMessageSenderId(FirebaseUtil.currentUserId());
        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
        chatMessageModel = new ChatMessageModel(chatMessage , FirebaseUtil.currentUserId() , Timestamp.now());
        FirebaseUtil.getChatRoomMessageReference(chatRoomId).add(chatMessageModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()){

                    msgInput.setText(""); //once the message is sent
                    //once the message is successfully sent then send the notification
                    sendNotificationFromFirebase(chatMessage);
                }
            }
        });

    }

    private void sendNotificationFromFirebase(String chatMessage) {
        //current username, message , current user id and other user token
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    UserModel currentUser = task.getResult().toObject(UserModel.class);
                    try{
                        JSONObject jsonObject  = new JSONObject();

                        JSONObject notificationObject = new JSONObject();
                        notificationObject.put("title" , currentUser.getUsername());
                        notificationObject.put("body" , chatMessage);

                        JSONObject dataObject  = new JSONObject();
                        dataObject.put("userId" , currentUser.getUserId());

                        jsonObject.put("notification" , notificationObject);
                        jsonObject.put("data"  , dataObject);
                        jsonObject.put("to" , otherUser.getFcmToken());
                        callApi(jsonObject);

                    }catch (Exception e){

                    }


                }
            }
        });


    }
    private void callApi(JSONObject jsonObject){
       MediaType JSON = MediaType.get("application/json");
        OkHttpClient client = new OkHttpClient();
        String url = "https://fcm.googleapis.com/fcm/send";
        RequestBody requestBody = RequestBody.create(jsonObject.toString(),JSON);
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .header("Authorization" , "Bearer AAAA5VYV75M:APA91bHin7PsmvAHp34tnuEVwHv3LinSi0IcyWnD3jA77-UDfEU5cOJxsUchRzE0tIhcnqnQnhbLl9Tbm22CpsS_mreFMEFgQBFUUsynHh-x6538rQWcuHwAEV5UIdmp-k6Pq2gedQcL")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

            }
        });

    }

    private void getOrSetChatRoom() {
        FirebaseUtil.getChatRoomReference(chatRoomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    //meaning that the chat room between those does not exist
                    chatRoomModel = task.getResult().toObject(ChatRoomModel.class);
                    if(chatRoomModel==null){
                        ArrayList<String>userIds = new ArrayList<>();
                        userIds.add(FirebaseUtil.currentUserId());
                        userIds.add(otherUser.getUserId());
                        //first time chatting
                        chatRoomModel = new ChatRoomModel(
                                chatRoomId ,
                                userIds ,
                                Timestamp.now() ,
                                ""  //here this will be empty at the time when there is no chatroom created

                        );

                        FirebaseUtil.getChatRoomReference(chatRoomId).set(chatRoomModel);
                    }
                }
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if(chatRecyclerAdapter!=null){
            chatRecyclerAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(chatRecyclerAdapter!=null){
            chatRecyclerAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(chatRecyclerAdapter!=null){
            chatRecyclerAdapter.startListening();
        }
    }
}