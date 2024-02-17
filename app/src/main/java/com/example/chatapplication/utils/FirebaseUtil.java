package com.example.chatapplication.utils;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.ktx.Firebase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.internal.StorageReferenceUri;

import java.text.SimpleDateFormat;
import java.util.List;

public class FirebaseUtil {

    public static String currentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }
    public static boolean isLoggedIn(){
        if (currentUserId() != null) {
            //means the user is logged in
            return true;
        }
        return false;
    }

    public static DocumentReference currentUserDetails(){
        return FirebaseFirestore.getInstance().collection("users").document(currentUserId());
    }
    public static CollectionReference allUserCollectionReference(){
        return  FirebaseFirestore.getInstance().collection("users");
    }
    public static DocumentReference getChatRoomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("chatrooms").document(chatroomId);
    }
    public static String getChatRoomId(String userId1 ,String userId2){
        if(userId1.hashCode()<userId2.hashCode()){
            return userId1 +"_"+userId2 ;
        }
        else{
            return userId2+"_"+userId1 ;
        }
    }
    public static CollectionReference getChatRoomMessageReference(String chatroomId){
        return getChatRoomReference(chatroomId).collection("chats");
    }
    public static  CollectionReference allChatRoomCollectionReference(){
        return  FirebaseFirestore.getInstance().collection("chatrooms");
    }
    public static DocumentReference getOtherUserFromChatRoom(List<String> userIds){
        if(userIds.get(0).equals(FirebaseUtil.currentUserId())){
            return allUserCollectionReference().document(userIds.get(1));
        }
        else{
           return allUserCollectionReference().document(userIds.get(0));
        }

    }
    public static String timestampToString(Timestamp timestamp){
        return new SimpleDateFormat("HH:MM").format(timestamp.toDate());
    }
    public static void logout(){
        FirebaseAuth.getInstance().signOut();
    }
    public static StorageReference getCurrentProfileStorageReference() {
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("profile_picture")
                .child(FirebaseUtil.currentUserId());


        reference.getMetadata().addOnSuccessListener(metadata -> {
            // Folder exists, you can proceed with the StorageReference
        }).addOnFailureListener(exception -> {
            // Folder does not exist
            if (((StorageException) exception).getErrorCode() == StorageException.ERROR_OBJECT_NOT_FOUND) {
                // Handle the scenario where the folder doesn't exist
                Log.e("Storage", "Profile picture folder does not exist for the current user.");
            }
        });

        return reference;
    }

    public static StorageReference getCurrentProfileOtherUserReference(String userIdOtherUser){
        return FirebaseStorage.getInstance().getReference().child("profile_picture")
                .child(userIdOtherUser);
    }
}
