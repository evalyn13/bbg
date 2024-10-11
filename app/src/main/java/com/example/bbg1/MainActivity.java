package com.example.bbg1;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount; // Add this import
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException; // Add this import
import com.google.android.gms.tasks.Task;





public class MainActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private CallbackManager callbackManager;
    private static final int RC_SIGN_IN = 9001; // Define a constant for Google sign-in

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

        // Facebook SDK initialization
        callbackManager = CallbackManager.Factory.create();

        showLoginDialog();
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
        ImageView googleSignInButton = dialogView.findViewById(R.id.googlesigninbtn); // Add Google button ID


        builder.setTitle("Login");

        AlertDialog dialog = builder.create();

        buttonLogin.setOnClickListener(v -> {
            String username = editTextUsername.getText().toString();
            String password = editTextPassword.getText().toString();
            // Implement your login logic here
            dialog.dismiss(); // Dismiss the dialog after handling login
        });

        // Google Sign-In Button Click
        googleSignInButton.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });

        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Handle Google Sign-In
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "Google sign-in successful!", Toast.LENGTH_SHORT).show();
            // Handle successful sign-in (e.g., update UI)
        } catch (ApiException e) {
            Toast.makeText(this, "Google sign-in failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
        }
    }
}



