package com.example.quote1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;

import android.view.MenuItem;
import android.widget.Button;
import java.util.*;

import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class HomeActivity extends AppCompatActivity {

    Button todo,addnote;
    ImageButton menuBtn;
    DocumentSnapshot document;
    private TextView randomDataTitleTextView,randomDataDescriptionTextView;
    private FirebaseFirestore firestore;

   // @SuppressLint({"MissingInflatedId", "WrongViewCast"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        todo=findViewById(R.id.todo_btn);
        addnote=findViewById(R.id.add_note);

        todo.setOnClickListener((v)->startActivity(new Intent(HomeActivity.this,MainActivity2.class)));
        addnote.setOnClickListener((v)->startActivity(new Intent(HomeActivity.this,MainActivity.class)));
        randomDataTitleTextView = findViewById(R.id.titleTextView);
        randomDataDescriptionTextView = findViewById(R.id.descriptionTextView);
        firestore = FirebaseFirestore.getInstance();

        menuBtn = findViewById(R.id.menu_btn);
        menuBtn.setOnClickListener((v)->showMenu() );
        getRandomRecordFromFirestore();


    }

    void showMenu(){
        PopupMenu popupMenu  = new PopupMenu(HomeActivity.this,menuBtn);
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if(menuItem.getTitle()=="Logout"){
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });

    }
    //this is random quote generator function
    private void getRandomRecordFromFirestore() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        firestore.collection("notes")
                .document(currentUser.getUid()).collection("my_notes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Get a random document from the query
                            int randomIndex = new Random().nextInt(queryDocumentSnapshots.size());
                            DocumentReference randomDocRef = queryDocumentSnapshots.getDocuments().get(randomIndex).getReference();

                            // Fetch the data from the random document
                            randomDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    if (documentSnapshot.exists()) {
                                        String title = documentSnapshot.getString("title");
                                        String description = documentSnapshot.getString("content");
                                        randomDataTitleTextView.setText("Title: "+title);
                                        randomDataDescriptionTextView.setText("Quote: "+ description);
                                    } else {
                                        randomDataDescriptionTextView.setText("No data found.");
                                    }
                                }
                            });
                        } else {
                            randomDataDescriptionTextView.setText("Add Quotes!");
                        }
                    }
                });
    }
}