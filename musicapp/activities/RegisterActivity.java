package com.example.musicapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicapp.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private final FirebaseAuth auth = FirebaseAuth.getInstance();
    private TextInputEditText emailEditText, passwordEditText, passwordConfirmEditText, nameEditText, bioEditText;
    private CheckBox artistCheckBox;
    private FirebaseUser currentUser;
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Get a reference to the collection you want to add the document to
    CollectionReference collectionRef = db.collection("users");
    View.OnClickListener registerBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            String email = Objects.requireNonNull(emailEditText.getText()).toString();
            String password = Objects.requireNonNull(passwordEditText.getText()).toString();
            String passwordConfirm = Objects.requireNonNull(passwordConfirmEditText.getText()).toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(passwordConfirm)) {
                Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "createUserWithEmail:success");
                            currentUser = auth.getCurrentUser();
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "createUserWithEmail:failure", task.getException());

                            String errorMessage = task.getException().toString();
                            String[] lines = errorMessage.split("\n");
                            String firstLine = lines[0];
                            String[] parts = firstLine.split(":");
                            String error = parts.length > 1 ? parts[1].trim() : firstLine;

                            Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    };

    private void updateUI() {
        // Create a new document with a custom ID
        String customId = currentUser.getUid();
        DocumentReference documentRef = collectionRef.document(customId);

        // Add entry to user collection
        Map<String, Object> user = new HashMap<>();
        user.put("name", nameEditText.getText().toString());
        user.put("bio", bioEditText.getText().toString());
        user.put("isArtist", artistCheckBox.isChecked());

        documentRef.set(user)
                .addOnSuccessListener(aVoid -> Log.d("Register", "Document added with ID: " + customId))
                .addOnFailureListener(e -> Log.w("Register", "Error adding document", e));

        recreate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        passwordConfirmEditText = findViewById(R.id.passwordConfirmEditText);
        nameEditText = findViewById(R.id.nameEditText);
        bioEditText = findViewById(R.id.bioEditText);
        artistCheckBox = findViewById(R.id.artistCheckBox);

        MaterialButton registerBtn = findViewById(R.id.registerButton);
        registerBtn.setOnClickListener(registerBtnListener);

        MaterialButton loginBtn = findViewById(R.id.loginActivityButton);
        loginBtn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

    }
}