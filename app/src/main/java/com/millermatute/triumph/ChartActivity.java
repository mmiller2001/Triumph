package com.millermatute.triumph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChartActivity extends AppCompatActivity implements Dialog.DialogListener {

    // Firebase Utilities
    private FirebaseUser currentUser;
    private String kanbanReference;

    // Firebase Adapters
    private FirebaseListAdapter<Task> adapterTask;

    // Recycler View Utilities
    private RecyclerView list_doing;
    private TaskAdapter taskAdapter;
    private FirebaseRecyclerOptions<Task> taskList;

    private ListView list_to_do;

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

            // Recycler View Instances
            list_doing = findViewById(R.id.list_doing);
            list_doing.setLayoutManager(new LinearLayoutManager(this));

            list_to_do = findViewById(R.id.list_to_do);
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
            displayListTo_Do();
            displayList_Doing();
        }
    }

    private void displayList_Doing() {
        taskList = new FirebaseRecyclerOptions.Builder<Task>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "/Data/To_Do"),Task.class)
                .build();

        taskAdapter = new TaskAdapter(taskList,this);
        list_doing.setAdapter(taskAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(list_doing);
    }

    private void displayListTo_Do() {
        FirebaseListOptions<Task> options = new FirebaseListOptions.Builder<Task>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "/Data/To_Do"), Task.class)
                .setLayout(R.layout.custom_task_list).build();

        adapterTask = new FirebaseListAdapter<Task>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Task model, int position) {
                CircleImageView task_person_image = (CircleImageView) v.findViewById(R.id.task_person_image);
                TextView task_person_creator = (TextView) v.findViewById(R.id.task_person_creator);
                TextView task_date_created = (TextView) v.findViewById(R.id.task_date_created);
                TextView task_date_modified = (TextView) v.findViewById(R.id.task_date_modified);
                TextView task_time_created = (TextView) v.findViewById(R.id.task_time_created);
                TextView task_title = (TextView) v.findViewById(R.id.task_title);
                TextView task_priority = (TextView) v.findViewById(R.id.task_priority);
                TextView task_description = (TextView) v.findViewById(R.id.task_description);
                TextView task_due_date = (TextView) v.findViewById(R.id.task_due_date);
                TextView task_date_completed = (TextView) v.findViewById(R.id.task_date_completed);

                task_person_creator.setText(model.getTask_person_creator());
                task_date_created.setText("Created: " + model.getTask_date_created());
                task_date_modified.setText("Modified: " + model.getTask_date_modified());
                task_time_created.setText(model.getTask_time_created());
                task_title.setText(model.getTask_title());
                task_priority.setText(model.getTask_priority());
                task_description.setText(model.getTask_description());
                task_due_date.setText("Due: " + model.getTask_due_date());
                task_date_completed.setText("Completed: " + model.getTask_date_completed());

                FirebaseDatabase.getInstance().getReference("Users").child(model.getTask_creator_uid())
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                User tempUser = snapshot.getValue(User.class);
                                if(tempUser.getPhotoUrl().equalsIgnoreCase("empty"))
                                    task_person_image.setImageResource(R.drawable.ic_baseline_person_24);
                                else {
                                    Glide.with(ChartActivity.this).load(tempUser.getPhotoUrl()).into(task_person_image);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }
        };

        list_to_do.setAdapter(adapterTask);
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
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Task newTask = new Task(task_title,task_description,task_due_date,
                task_date_completed,task_priority,userChat.getDisplayName(),
                userChat.getDisplayName(),userID,userID);
        FirebaseDatabase.getInstance().getReference("Kanban/" + kanbanReference + "/Data/To_Do").push().setValue(newTask);
    }

    // Currently working
    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END,0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = target.getAdapterPosition();
//
//            Collections.swap(taskList, fromPosition, toPosition);
//            list_doing.getAdapter().notifyItemMoved(fromPosition,toPosition);
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) { // Left or Right

        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            adapterTask.startListening();
            taskAdapter.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (currentUser != null) {
            adapterTask.stopListening();
            taskAdapter.stopListening();
        }
    }
}