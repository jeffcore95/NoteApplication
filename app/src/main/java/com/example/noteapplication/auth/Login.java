package com.example.noteapplication.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapplication.MainActivity;
import com.example.noteapplication.databinding.ActivityLoginBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {
    ActivityLoginBinding binding;
    FirebaseAuth fAuth;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        fAuth = FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        getSupportActionBar().setTitle("Login to Note Application");


        if(getIntent().getStringExtra("firstTimeLogin").equals("false")){
            showWarning();
        }
        binding.loginBtn.setOnClickListener(v -> {
            String loginEmail = binding.loginEmail.getText().toString();
            String loginPassword = binding.loginPassword.getText().toString();

            if(loginEmail.isEmpty() || loginPassword.isEmpty()){
                Toast.makeText(Login.this, "Field are Required.", Toast.LENGTH_SHORT).show();
                return;
            }
            binding.progressBarLogin.setVisibility(View.VISIBLE);

            fAuth.signInWithEmailAndPassword(loginEmail,loginPassword).addOnSuccessListener(authResult -> {
                Toast.makeText(Login.this, "Login Successful !", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(Login.this, "Login Failed. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.progressBarLogin.setVisibility(View.GONE);
            });
        });
        binding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Register.class));
            }
        });
    }

    private void showWarning() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are You Sure?")
                .setMessage("Linking the existed account will discard all the temp notes, create a new Account to save them.")
                .setPositiveButton("Save Notes", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }).setNegativeButton("I don't care", (dialog, which) -> {
                    // ToDo: delete all the notes created by Anonymous user
                    // ToDo: delete the Anonymous user
                    if(fAuth.getCurrentUser().isAnonymous()){
                        final FirebaseUser user = fAuth.getCurrentUser();

                        //db.collection("notes").document(user.getUid()).delete().addOnSuccessListener(unused -> Toast.makeText(Login.this, "All Temp Notes Deleted.", Toast.LENGTH_SHORT).show());
                        user.delete().addOnSuccessListener(unused -> Toast.makeText(Login.this, "Temp User Deleted.", Toast.LENGTH_SHORT).show());
                    }
                });

        warning.show();
    }

}
