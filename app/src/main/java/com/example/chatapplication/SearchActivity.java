package com.example.chatapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chatapplication.adapter.SearchUserAdapter;
import com.example.chatapplication.model.UserModel;
import com.example.chatapplication.utils.FirebaseUtil;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {
    EditText searchInput ;
    ImageView imgBack , imgSearch ;
    RecyclerView recyclerView;
    SearchUserAdapter searchUserAdapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initUI();
    }

    private void initUI() {
        searchInput  = findViewById(R.id.edtxSearchuser);
        imgSearch = findViewById(R.id.search_user_button);
        imgBack   =findViewById(R.id.btn_back);
        recyclerView = findViewById(R.id.rv_search);
        searchInput.requestFocus();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onBackPressed();
            }
        });
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                handleSearch();
            }
        });

    }

    private void handleSearch() {
        String searchName = searchInput.getText().toString();
        if(searchName.isEmpty() || searchName.length()<3){
            searchInput.setError("Invalid Username");
            return;
        }
        setupSearchRecyclerView(searchName);
    }
    private void setupSearchRecyclerView(String searchName){
        //search name is the search term
        Query query = FirebaseUtil.allUserCollectionReference()
                .whereGreaterThanOrEqualTo("username" , searchName).whereLessThanOrEqualTo("username" , searchName + '\uf8ff');
        FirestoreRecyclerOptions<UserModel> options  = new FirestoreRecyclerOptions.Builder<UserModel>()
                .setQuery(query,UserModel.class).build();

        //Get the users from the Firestore and populate in rv
        searchUserAdapter = new SearchUserAdapter(options,  getApplicationContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchUserAdapter);
        searchUserAdapter.startListening();




    }

    @Override
    protected void onStart() {
        super.onStart();
        if(searchUserAdapter!=null){
            searchUserAdapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(searchUserAdapter!=null){
            searchUserAdapter.stopListening();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(searchUserAdapter!=null){
            searchUserAdapter.notifyDataSetChanged();
        }
    }

}