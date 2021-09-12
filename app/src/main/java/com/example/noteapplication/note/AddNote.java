package com.example.noteapplication.note;

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
import com.example.noteapplication.databinding.ActivityAddNoteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore db;
    ActivityAddNoteBinding binding;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityAddNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);


        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        binding.fab.setOnClickListener(view -> {
            String addTitle = binding.addNoteTitle.getText().toString();
            String addContent = binding.contentAddNote.editText.getText().toString();

            if(addTitle.isEmpty() || addContent.isEmpty()){
                Toast.makeText(AddNote.this,"Please do not left empty field.", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.contentAddNote.progressBar.setVisibility(View.VISIBLE);

            //firebase save note
            DocumentReference documentReference = db.collection("notes").document(user.getUid()).collection("myNotes").document();
            Map<String,Object> note = new HashMap<>();
            note.put("title",addTitle);
            note.put("content",addContent);

            documentReference.set(note).addOnSuccessListener(unused -> {
                Toast.makeText(AddNote.this, "Note Added", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(AddNote.this, MainActivity.class));
            }).addOnFailureListener(e -> {
                Toast.makeText(AddNote.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                binding.contentAddNote.progressBar.setVisibility(View.VISIBLE);
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
            Toast.makeText(this, "Note Discard.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(AddNote.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }
}