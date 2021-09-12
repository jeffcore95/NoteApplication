package com.example.noteapplication.auth;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapplication.MainActivity;
import com.example.noteapplication.R;
import com.example.noteapplication.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class Register extends AppCompatActivity {
    ActivityRegisterBinding binding;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportActionBar()).setTitle("Register New Account");

        fAuth = FirebaseAuth.getInstance();

        binding.loginHere.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(),Login.class)));

        binding.btnSync.setOnClickListener(v -> {
            String userName = binding.registerName.getText().toString();
            String userEmail = binding.registerEmail.getText().toString();
            String userPassword = binding.registerPassword.getText().toString();
            String userConfirmPassword = binding.registerConfirmPassword.getText().toString();

            if(userName.isEmpty() || userEmail.isEmpty() || userPassword.isEmpty() || userConfirmPassword.isEmpty()){
                Toast.makeText(Register.this, "All Field Are Required.", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!userPassword.equals(userConfirmPassword)){
                 binding.registerConfirmPassword.setError("Password Do Not Match");
                 return;
            }

            binding.progressBarRegister.setVisibility(View.VISIBLE);

            AuthCredential credential = EmailAuthProvider.getCredential(userEmail,userPassword);
            Objects.requireNonNull(fAuth.getCurrentUser()).linkWithCredential(credential).addOnSuccessListener(authResult -> {
                Toast.makeText(Register.this,"Notes are Synced.", Toast.LENGTH_SHORT).show();

                FirebaseUser user = fAuth.getCurrentUser();
                Intent intent = new Intent(Register.this,MainActivity.class);
                intent.putExtra("username",userName);
                startActivity(intent);



            }).addOnFailureListener(e -> {
                Toast.makeText(Register.this, "Failed to Connect, please try again.", Toast.LENGTH_SHORT).show();
                binding.progressBarRegister.setVisibility(View.GONE);
            });
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close){
            Toast.makeText(this, "Cancel Sync Note", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}