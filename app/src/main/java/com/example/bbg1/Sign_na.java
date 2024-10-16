package com.example.bbg1;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class Sign_na extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up); // Ensure this matches your sign-up layout file

        // Find views by ID
        EditText usernameInput = findViewById(R.id.username_input);
        EditText emailInput = findViewById(R.id.emailsignup);
        EditText passwordInput = findViewById(R.id.password_input);
        Button signUpButton = findViewById(R.id.sign_up_btn);

        // Set an OnClickListener for the sign-up button
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = usernameInput.getText().toString().trim();
                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Simple validation
                if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(Sign_na.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle successful sign-up (you can add your logic here)
                    Toast.makeText(Sign_na.this, "Sign Up Successful!", Toast.LENGTH_SHORT).show();

                    // Close the sign-up activity and return to the previous activity
                    finish(); // This will return to the MainActivity where the login dialog is shown
                }
            }
        });
    }
}

