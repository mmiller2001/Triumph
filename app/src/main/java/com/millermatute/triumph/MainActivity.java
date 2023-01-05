package com.millermatute.triumph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseListAdapter;
import com.firebase.ui.database.FirebaseListOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private BottomSheetBehavior mBottomSheetBehavior;

    private ArrayAdapter<String> arrayAdapter;
    private ListView listView;
    private ListView kanban_list;
    private ProgressBar progressBar;

    // Kanban Utilities
    private TextView kanban_title;
    private TextView kanban_description;
    private TextView kanban_search;
    private ImageView kanban_save;

    // Firebase Services
    private FirebaseListAdapter<User> adapter;
    private FirebaseListAdapter<Kanban> adapterKanban;
    private FirebaseUser currentUser;
    private User userChat;

    //Google Services
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
        } else {
            checkCurrentUser();

            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
            );

            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            progressBar = findViewById(R.id.progress_bar);
            progressBar.setVisibility(View.VISIBLE);

            listView = findViewById(R.id.listview);
            kanban_list = findViewById(R.id.list_kanban);

            // Kanban Utilities
            kanban_title = findViewById(R.id.kanban_title);
            kanban_description = findViewById(R.id.kanban_description);
            kanban_search = findViewById(R.id.kanban_search);
            kanban_save = findViewById(R.id.kanban_save);

            kanban_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    newKanban();
                }
            });

            View bottomSheet = findViewById(R.id.bottom_sheet);
            mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
            mBottomSheetBehavior.setPeekHeight(200);
            mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            displayMembers();
            displayKanbans();
        }
    }

    private void checkCurrentUser() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase.getInstance().getReference("Users").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userChat = snapshot.getValue(User.class);
                Toast.makeText(MainActivity.this, "Welcome: " + userChat.getDisplayName(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "Error: No User Sync", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newKanban() {
        Kanban kanban = new Kanban(kanban_title.getText().toString(),kanban_description.getText().toString());
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String key = FirebaseDatabase.getInstance().getReference("Kanban").push().getKey();
        FirebaseDatabase.getInstance().getReference("Kanban").child(key).child("Information").setValue(kanban);
        FirebaseDatabase.getInstance().getReference("GroupID").child(userID).child(key).setValue(kanban);

        Toast.makeText(this, "Kanban key: " + key, Toast.LENGTH_SHORT).show();

        kanban_title.setText("");
        kanban_description.setText("");
        kanban_search.setText("");
    }

    private void displayKanbans() {
        String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseListOptions<Kanban> options = new FirebaseListOptions.Builder<Kanban>()
                .setQuery(FirebaseDatabase.getInstance().getReference("GroupID" + "/" + userID).orderByChild("kanban_title"), Kanban.class)
                .setLayout(R.layout.custom_kanban_project).build();

        adapterKanban = new FirebaseListAdapter<Kanban>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Kanban model, int position) {
                TextView kanban_title = (TextView) v.findViewById(R.id.kanban_title);
                CircleImageView kanban_image = (CircleImageView) v.findViewById(R.id.kanban_image);

                kanban_title.setText(model.getKanban_title());
                kanban_image.setImageResource(R.drawable.ic_baseline_view_kanban_24);
            }
        };

        kanban_list.setAdapter(adapterKanban);
        progressBar.setVisibility(View.GONE);

        kanban_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                DatabaseReference itemRef = adapterKanban.getRef(position);
//                Toast.makeText(MainActivity.this, "Item: " + itemRef.getKey(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(MainActivity.this, ChartActivity.class);
                intent.putExtra("KanbanReference",itemRef.getKey());
                startActivity(intent);
            }
        });
    }

    private void displayMembers() {

        FirebaseListOptions<User> options = new FirebaseListOptions.Builder<User>()
                .setQuery(FirebaseDatabase.getInstance().getReference("Users").orderByChild("givenName"), User.class)
                .setLayout(R.layout.custom_triumph_member).build();

        adapter = new FirebaseListAdapter<User>(options) {
            @Override
            protected void populateView(@NonNull View v, @NonNull User model, int position) {
                TextView member_name = (TextView) v.findViewById(R.id.member_name);
                TextView member_email = (TextView) v.findViewById(R.id.member_email);
                CircleImageView member_picture = v.findViewById(R.id.member_picture);

                member_name.setText(model.getGivenName());
                member_email.setText(model.getEmail());
                if(model.getPhotoUrl().equalsIgnoreCase("empty")) member_picture.setImageResource(R.drawable.ic_baseline_person_24);
                else {
                    Glide.with(MainActivity.this).load(model.getPhotoUrl()).into(member_picture);
                }
            }
        };

        listView.setAdapter(adapter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.notifications:
                Toast.makeText(this, "Notifications selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout:
                Toast.makeText(this, "Logout selected", Toast.LENGTH_SHORT).show();
                SignOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void SignOut() {
        FirebaseAuth.getInstance().signOut();
//        googleSignInClient.signOut();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this,googleSignInOptions);
        googleSignInClient.signOut();

        Toast.makeText(this, "Successfully disconnected.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null) {
            adapter.startListening();
            adapterKanban.startListening();
        }
    }
    @Override
    public void onStop() {
        super.onStop();
        if (currentUser != null) {
            adapter.stopListening();
            adapterKanban.stopListening();
        }
    }

}