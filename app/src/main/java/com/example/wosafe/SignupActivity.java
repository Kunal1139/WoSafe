package com.example.wosafe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    EditText signupName, signupUsername, signupEmail, signupPassword;
    TextView loginRedirectText;
    Button signupButton;
    FirebaseDatabase database;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // Initialize UI components
        signupName = findViewById(R.id.signup_name);
        signupEmail = findViewById(R.id.signup_email);
        signupUsername = findViewById(R.id.signup_username);
        signupPassword = findViewById(R.id.signup_password);
        loginRedirectText = findViewById(R.id.loginRedirectText);
        signupButton = findViewById(R.id.signup_button);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve input values
                String name = signupName.getText().toString().trim();
                String email = signupEmail.getText().toString().trim();
                String username = signupUsername.getText().toString().trim();
                String password = signupPassword.getText().toString().trim();

                // Input validation
                if (name.isEmpty() || email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Please fill all fields!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(SignupActivity.this, "Enter a valid email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(SignupActivity.this, "Password must be at least 6 characters long!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Save data to Firebase
                database = FirebaseDatabase.getInstance();
                reference = database.getReference("users");

                Map<String, Object> user = new HashMap<>();
                user.put("name", name);
                user.put("email", email);
                user.put("username", username);
                user.put("password", password);

                reference.child(username).setValue(user).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(SignupActivity.this, "Signup successful!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish(); // Prevent returning to signup
                    } else {
                        Toast.makeText(SignupActivity.this, "Signup failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseError", "Error: ", task.getException());
                    }
                });
            }
        });

        loginRedirectText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
