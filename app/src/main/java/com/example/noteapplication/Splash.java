package com.example.noteapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapplication.auth.Login;
import com.example.noteapplication.databinding.ActivitySplashBinding;
import com.google.firebase.auth.FirebaseAuth;

public class Splash extends AppCompatActivity {
    FirebaseAuth fAuth;
    ActivitySplashBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        super.onCreate(savedInstanceState);
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            intent.putExtra("firstTimeLogin","true");
            startActivity(intent);
            //startActivity(new Intent(getApplicationContext(), Login.class));

        });

        binding.anonyLoginBtn.setOnClickListener(v -> {
            // create new anonymous account
            fAuth.signInAnonymously().addOnSuccessListener(authResult -> {
                Toast.makeText(Splash.this, "Logged in with Temporary Account", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }).addOnFailureListener(e -> {
                Toast.makeText(Splash.this, "Error with "+e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            });
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            fAuth =FirebaseAuth.getInstance();
            // check if the user is logged in

            if(fAuth.getCurrentUser() != null){
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }else{
                binding.loginBtn.setVisibility(View.VISIBLE);
                binding.anonyLoginBtn.setVisibility(View.VISIBLE);
                binding.progressBar2.setVisibility(View.GONE);
            }
        },2000);
    }
}