package com.millermatute.triumph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ChartActivity extends AppCompatActivity implements Dialog.DialogListener {

    // Firebase Utilities
    private FirebaseUser currentUser;
    private String kanbanReference;

    private ListView list_to_do;
    private ListView list_doing;
    private ListView list_done;

    private ProgressBar progressBar;
    private FloatingActionButton kanban_plus;

    // Classes
    private User userChat;

    private MaterialToolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(ChartActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            if (savedInstanceState == null) {
                Bundle extras = getIntent().getExtras();
                if(extras == null) {
                    kanbanReference = null;
                }
                else {
                    kanbanReference = extras.getString("KanbanReference");
                }
            }
            else {
                kanbanReference = (String) savedInstanceState.getSerializable("KanbanReference");
            }

            getCurrentUser();

            toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(ChartActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            });

            progressBar = findViewById(R.id.progress_bar);
            kanban_plus = findViewById(R.id.kanban_plus);

            list_to_do = findViewById(R.id.list_to_do);
            list_doing = findViewById(R.id.list_doing);
            list_done = findViewById(R.id.list_done);

            progressBar.setVisibility(View.VISIBLE);

            kanban_plus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Dialog dialog = new Dialog();
                    dialog.show(getSupportFragmentManager(), "example dialog");

                }
            });

            syncKanban();
        }
    }

    private void getCurrentUser() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChat = snapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChartActivity.this, "Error: No User Sync", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void syncKanban() {
        FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "/Information").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Kanban newKanban = snapshot.getValue(Kanban.class);
                toolbar.setTitle(newKanban.getKanban_title());
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChartActivity.this, "Error: Couldn't sync Kanban", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void applyText(String task_title, String task_description, String task_due_date, String task_date_completed, String task_priority) {
        Task newTask = new Task(task_title,task_description,task_due_date,task_date_completed,task_priority,userChat.getDisplayName(),userChat.getDisplayName());
        //String key = FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "Data/To_Do").push().getKey();
        FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "/Data/To_Do").push().setValue(newTask);
//        Toast.makeText(this, "Task Title: " + newTask.getTask_title()
//                + "\nTask Description: " + newTask.getTask_description()
//                + "\nTask Due Date: " + newTask.getTask_due_date()
//                + "\nTask Date Completed: " + newTask.getTask_date_completed()
//                + "\nTask Priority: " + newTask.getTask_priority()
//                + "\nTask Creator: " + newTask.getTask_person_creator()
//                + "\nTask Modifier: " + newTask.getTask_person_modifier()
//                + "\nTask Date Created: " + newTask.getTask_date_created()
//                + "\nTask Date Modified: " + newTask.getTask_date_modified()
//                + "\nTask Time Created: " + newTask.getTask_time_created()
//                + "\nTask Time Modified: "+ newTask.getTask_time_modified()
//                + "\nTask Zone: "+ newTask.getTask_zone(), Toast.LENGTH_LONG).show();
    }
}