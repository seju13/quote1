package com.example.quote1;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.quote1.Adapter.ToDoAdapter;
import com.example.quote1.Model.ToDoModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class MainActivity2 extends AppCompatActivity implements OnDialogCloseListner{

    private RecyclerView recyclerView;
    private FloatingActionButton mFab;
    private FirebaseFirestore firestore;
    private ToDoAdapter adapter;
    private List<ToDoModel> mList;
    private Query query;
    private ListenerRegistration listenerRegistration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        recyclerView = findViewById(R.id.recycerlview);
        mFab = findViewById(R.id.floatingActionButton);
        firestore = FirebaseFirestore.getInstance();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity2.this));

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewTask.newInstance().show(getSupportFragmentManager() , AddNewTask.TAG);
            }
        });

        mList = new ArrayList<>();
        adapter = new ToDoAdapter(MainActivity2.this , mList);



        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(adapter));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        showData();
        recyclerView.setAdapter(adapter);

//// Define a reference to your Firestore collection.
//        CollectionReference tasksCollection = FirebaseFirestore.getInstance().collection("tasks");
//
//// Create a query to get all tasks.
//        Query query = tasksCollection;
//
//// Execute the query and retrieve the task due date and task ID for each task.
//        query.get()
//                .addOnSuccessListener(queryDocumentSnapshots -> {
//                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
//                        String taskId = document.getId(); // Get the task ID
//                        Date dueDate = document.getDate("dueDate"); // Get the due date field from Firestore
//                        // Now you have the task ID and due date, you can schedule notifications as mentioned earlier.
//
//                        // When retrieving tasks from Firestore:
//// Calculate the notification date (one day before the due date).
//                        Calendar notificationDate = Calendar.getInstance();
//                        notificationDate.setTime(dueDate); // Set to task's due date
//                        notificationDate.add(Calendar.DAY_OF_MONTH, -1); // Subtract one day
//
//// Schedule a notification for each task.
//                        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
//                        Intent notificationIntent = new Intent(this, MainActivity2.class);
//                        notificationIntent.putExtra("taskId", taskId); // Pass task ID to identify the task in the receiver
//                        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, Integer.parseInt(taskId), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        alarmManager.set(AlarmManager.RTC_WAKEUP, notificationDate.getTimeInMillis(), pendingIntent);
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    // Handle any errors that may occur during the query.
//                });



    }
    private void showData(){
        query = firestore.collection("task").orderBy("time" , Query.Direction.DESCENDING);

        listenerRegistration = query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                for (DocumentChange documentChange : value.getDocumentChanges()){
                    if (documentChange.getType() == DocumentChange.Type.ADDED){
                        String id = documentChange.getDocument().getId();
                        ToDoModel toDoModel = documentChange.getDocument().toObject(ToDoModel.class).withId(id);
                        mList.add(toDoModel);
                        adapter.notifyDataSetChanged();
                    }
                }
                listenerRegistration.remove();

            }
        });
    }

    @Override
    public void onDialogClose(DialogInterface dialogInterface) {
        mList.clear();
        showData();
        adapter.notifyDataSetChanged();
    }
}
