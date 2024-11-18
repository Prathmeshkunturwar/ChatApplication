package com.learnoset.chatapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Login extends AppCompatActivity {

    private DatabaseReference databaseReference; // Reference to the Firebase database
    private MyProgressDialog progressDialog; // Progress bar for login process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Set the layout for the login activity

        // Initializing input fields and buttons from the layout
        final EditText mobileET = findViewById(R.id.l_mobile);
        final EditText passwordET = findViewById(R.id.l_password);
        final AppCompatButton loginNowBtn = findViewById(R.id.l_LoginBtn);
        final TextView registerNowTV = findViewById(R.id.l_registerNowBtn);

        // Obtain the reference to the Firebase database using the defined URL
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url));

        // Check if user is already logged in. If yes, open MainActivity; otherwise, user needs to register
        if (!MemoryData.getMobile(this).isEmpty()) {
            openMainActivity();
        }

        // Create and configure a progress dialog for the login process
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);

        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Retrieve user's entered data from EditText
                final String mobileTxt = mobileET.getText().toString();
                final String passwordTxt = passwordET.getText().toString();

                // Check if user's entered mobile number and password are empty or not
                if (mobileTxt.isEmpty() || passwordTxt.isEmpty()) {
                    Toast.makeText(Login.this, "Please enter mobile or password", Toast.LENGTH_SHORT).show();
                } else {
                    // Login the user
                    loginUser(mobileTxt, passwordTxt);
                }
            }
        });

        registerNowTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Register activity and finish the Login activity
                startActivity(new Intent(Login.this, Register.class));
                finish();
            }
        });
    }

    private void loginUser(String mobileNumber, String userEnteredPassword) {

        // Show progress bar
        progressDialog.show();

        // Retrieve data from Firebase to validate user's login
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss(); // Hide progress dialog

                // Check if the user's mobile number exists in the database
                if (!snapshot.child("users").hasChild(mobileNumber)) {
                    Toast.makeText(Login.this, "Mobile number does not exist in the database", Toast.LENGTH_SHORT).show();
                } else {
                    // Get user's saved password from the database
                    final String password = snapshot.child("users").child(mobileNumber).child("password").getValue(String.class);

                    if (password != null) {
                        // Check if user's entered password matches the password stored in the database
                        if (password.equals(userEnteredPassword)) {
                            // Successful login
                            Toast.makeText(Login.this, "Login successful", Toast.LENGTH_SHORT).show();

                            // Save user's mobile number for future logins
                            MemoryData.saveMobile(mobileNumber, Login.this);

                            // Open the MainActivity and finish the Login activity
                            openMainActivity();
                        } else {
                            // Password mismatch
                            Toast.makeText(Login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        // Unable to retrieve user's password from the database
                        Toast.makeText(Login.this, "Unable to get user's password from the database", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
                // Display a message for database error
                Toast.makeText(Login.this, "Database error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openMainActivity() {
        // Open the MainActivity and finish the Login activity
        startActivity(new Intent(Login.this, MainActivity.class));
        finish();
    }
}