package com.learnoset.chatapplication;

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

public class Register extends AppCompatActivity {

    private DatabaseReference databaseReference; // Reference to the Firebase database
    private MyProgressDialog progressDialog; // Progress bar for registration process

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Set the layout for the registration activity

        // Initializing input fields and buttons from the layout
        final EditText name = findViewById(R.id.r_name);
        final EditText mobile = findViewById(R.id.r_mobile);
        final EditText email = findViewById(R.id.r_email);
        final EditText password = findViewById(R.id.r_password);
        final AppCompatButton registerBtn = findViewById(R.id.r_registerBtn);
        final TextView loginNowBtn = findViewById(R.id.r_loginNowBtn);

        // Obtain the reference to the Firebase database using the defined URL
        databaseReference = FirebaseDatabase.getInstance().getReferenceFromUrl(getString(R.string.database_url));

        // Create and configure a progress dialog for registration process
        progressDialog = new MyProgressDialog(this);
        progressDialog.setCancelable(false);

        // Register button click listener
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Retrieve user details from input fields
                final String nameTxt = name.getText().toString();
                final String mobileTxt = mobile.getText().toString();
                final String emailTxt = email.getText().toString();
                final String passwordTxt = password.getText().toString();

                // Check if any field is empty
                if (nameTxt.isEmpty() || mobileTxt.isEmpty() || emailTxt.isEmpty() || passwordTxt.isEmpty()) {
                    // Display a message if any field is empty
                    Toast.makeText(Register.this, "All Fields Required!!!", Toast.LENGTH_SHORT).show();
                } else if (!isEmailValid(emailTxt)) {
                    // Display a message if the email format is invalid
                    Toast.makeText(Register.this, "Invalid Email Format", Toast.LENGTH_SHORT).show();
                } else {

                    // Show progress dialog
                    progressDialog.show();

                    // Check if the user's mobile number already exists in the database
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            progressDialog.dismiss(); // Hide the progress dialog

                            if (snapshot.child("users").hasChild(mobileTxt)) {
                                // Display a message if mobile number already exists
                                Toast.makeText(Register.this, "Mobile already exists", Toast.LENGTH_SHORT).show();
                            } else {

                                // If mobile number doesn't exist, proceed with registration
                                Toast.makeText(Register.this, "Registered successfully", Toast.LENGTH_SHORT).show();

                                // Save user details in the database
                                databaseReference.child("users").child(mobileTxt).child("email").setValue(emailTxt);
                                databaseReference.child("users").child(mobileTxt).child("name").setValue(nameTxt);
                                databaseReference.child("users").child(mobileTxt).child("password").setValue(passwordTxt);

                                // Save user's mobile number for future login
                                MemoryData.saveMobile(mobileTxt, Register.this);

                                // Open the MainActivity and finish the Register activity
                                startActivity(new Intent(Register.this, MainActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            progressDialog.dismiss();

                            // Display a message for database error
                            Toast.makeText(Register.this, "Database error", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        // "Login Now" button click listener
        loginNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open the Login activity and finish the Register activity
                startActivity(new Intent(Register.this, Login.class));
                finish();
            }
        });
    }

    // Function to validate the email format
    private boolean isEmailValid(String email) {
        // Regular expression for email validation
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        // Check if the email matches the pattern
        return email.matches(emailPattern);
    }
}
