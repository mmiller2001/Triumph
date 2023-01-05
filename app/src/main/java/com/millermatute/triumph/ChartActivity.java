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

public class ChartActivity extends AppCompatActivity {

    // Firebase Utilities
    private FirebaseUser currentUser;
    private String kanbanReference;

    private ListView list_to_do;
    private ListView list_doing;
    private ListView list_done;

    private ProgressBar progressBar;
    private FloatingActionButton kanban_plus;

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
//                    LayoutInflater inflater = (LayoutInflater) ChartActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    View popupView = inflater.inflate(R.layout.popup_task, null);
//
//                    int width = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    int height = LinearLayout.LayoutParams.WRAP_CONTENT;
//                    boolean focusable = true; // lets taps outside the popup also dismiss it
//                    final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
//
//                    popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        popupWindow.setElevation(20);
//                    }
//
//                    popupView.setOnTouchListener(new View.OnTouchListener() {
//                        @Override
//                        public boolean onTouch(View view, MotionEvent motionEvent) {
//                            popupWindow.dismiss();
//                            return true;
//                        }
//                    });
                }
            });

            syncKanban();

        }
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
}