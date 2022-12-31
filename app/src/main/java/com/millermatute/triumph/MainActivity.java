package com.millermatute.triumph;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private TextView user_email;
    private TextView signOut;
    private CircleImageView profilePic;
    private FirebaseAuth mAuth;

    // Google Profile Information
    private TextView user_displayName;
    private TextView user_givenName;
    private TextView user_familyName;
    private TextView user_id;

    //Google Services
    GoogleSignInOptions googleSignInOptions;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user_email = findViewById(R.id.user_email);
        signOut = findViewById(R.id.signOut);
        profilePic = findViewById(R.id.profilePic);
        mAuth = FirebaseAuth.getInstance();

        user_displayName = findViewById(R.id.user_displayName);
        user_givenName = findViewById(R.id.user_givenName);
        user_familyName = findViewById(R.id.user_familyName);
        user_id = findViewById(R.id.user_id);

        String email_welcome = FirebaseAuth.getInstance().getCurrentUser().getEmail();
        user_email.setText(email_welcome);

        // Firebase Sync
        FirebaseSync();

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SignOut();
            }
        });
    }

    private void SignOut() {
        FirebaseAuth.getInstance().signOut();

        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(MainActivity.this,googleSignInOptions);
        googleSignInClient.signOut();

        Toast.makeText(this, "Successfully disconnected.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }

    private void FirebaseSync() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser account = firebaseAuth.getCurrentUser();
        if (account != null){
            //Display User Image from Google Account
            //Objects.requireNonNull() prevents getPhotoUrl() from returning a NullPointerException
            if(account.getPhotoUrl() == null) {
                profilePic.setImageResource(R.drawable.ic_launcher_background);
//                Toast.makeText(this, FirebaseAuth.getInstance().getCurrentUser().getUid(), Toast.LENGTH_LONG).show();

                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
                reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User userChat = snapshot.getValue(User.class); // mistake here
                        if(userChat != null) {
                            user_displayName.setText(userChat.getDisplayName());
                            user_givenName.setText(userChat.getGivenName());
                            user_familyName.setText(userChat.getFamilyName());
                            user_id.setText(userChat.getId());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MainActivity.this, "Something did not go right!", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {
                String personImage = Objects.requireNonNull(account.getPhotoUrl()).toString();
                Glide.with(this).load(personImage).into(profilePic);

                GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(MainActivity.this);
                if(acct != null) {
                    user_displayName.setText(acct.getDisplayName());
                    user_givenName.setText(acct.getGivenName());
                    user_familyName.setText(acct.getFamilyName());
                    user_id.setText(acct.getId());
                }
            }
        }
    }
}