package com.example.bbg1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Google Sign-In configuration
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Initialize the ActivityResultLauncher
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        handleSignInResult(task);
                    }
                });

        // Check if the user is logged in
        boolean isLoggedIn = getIntent().getBooleanExtra("isLoggedIn", false);

        if (!isLoggedIn) {
            showLoginDialog();
        } else {
            startGame(null); // Start the game directly if logged in
        }
    }

    public void startGame(View view) {
        GameView gameView = new GameView(this);
        setContentView(gameView);
    }

    private void showLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.background_login, null);
        builder.setView(dialogView);

        EditText editTextUsername = dialogView.findViewById(R.id.username_input);
        EditText editTextPassword = dialogView.findViewById(R.id.password_input);
        Button buttonLogin = dialogView.findViewById(R.id.login_btn);
        ImageView googleSignInButton = dialogView.findViewById(R.id.googlesigninbtn);
        TextView signUpText = dialogView.findViewById(R.id.signup_text); // Find the sign-up TextView

        builder.setTitle("Login");

        AlertDialog dialog = builder.create();

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();

            // Simple validation
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
            } else {
                // Implement your login logic here
                dialog.dismiss(); // Dismiss the dialog after handling login
                startGame(null); // Start the game after successful login
            }
        });

        // Google Sign-In Button Click
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            googleSignInLauncher.launch(signInIntent); // Use the launcher instead
        });

        // Sign Up Click
        signUpText.setOnClickListener(v -> {
            dialog.dismiss(); // Dismiss the dialog before starting the sign-up activity
            Intent intent = new Intent(MainActivity.this, Sign_na.class);
            startActivity(intent);
        });

        dialog.show();
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show();
            // Handle successful sign-in (e.g., update UI)
            startGame(null); // Start the game after successful sign-in
        } catch (ApiException e) {
            Toast.makeText(this, "Google sign-in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
}

