package com.millermatute.triumph;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Random;

public class RegistrationActivity extends AppCompatActivity {

    private EditText email_editText;
    private EditText password_editText;
    private EditText firstName_editText;
    private EditText lastName_editText;
    private Button submitButton;
    private TextView login;

    // Google Services
//    private ImageView google_img;
//    GoogleSignInOptions googleSignInOptions;
//    GoogleSignInClient googleSignInClient;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        firstName_editText = findViewById(R.id.firstName);
        lastName_editText = findViewById(R.id.lastName);
        email_editText = findViewById(R.id.email);
        password_editText = findViewById(R.id.password);
        submitButton = findViewById(R.id.buttonSubmit);
        login = findViewById(R.id.login);

        mAuth = FirebaseAuth.getInstance();

        // Regular Mail Services
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerNewUser();
            }
        });
    }

    private void registerNewUser() {

        String firstName = firstName_editText.getText().toString();
        String lastName = lastName_editText.getText().toString();
        String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();



        mAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            int random = new Random().nextInt(61) + 20; // [0, 60] + 20 => [20, 80]
                            User user = new User(firstName + " " + lastName,firstName,lastName,email,String.valueOf(random),"empty");

                            // Adding user to Realtime Database
                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast.makeText(RegistrationActivity.this, "Welcome " + user.getDisplayName(), Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(RegistrationActivity.this, "Error w/ Email Sign Up", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                            Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(RegistrationActivity.this, "Error. Couldn't Register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}