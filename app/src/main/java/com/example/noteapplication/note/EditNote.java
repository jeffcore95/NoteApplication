package com.example.noteapplication.note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.noteapplication.MainActivity;
import com.example.noteapplication.R;
import com.example.noteapplication.databinding.ActivityEditNoteBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditNote extends AppCompatActivity {
    ActivityEditNoteBinding binding;
    FirebaseFirestore db;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        String editTitle = getIntent().getStringExtra("title");
        String editContent = getIntent().getStringExtra("content");

        setSupportActionBar(binding.toolbar);

        binding.editNoteTitle.setText(editTitle);
        binding.editNoteContent.setText(editContent);

        binding.saveEditNote.setOnClickListener(v -> {
            String newUpdateTitle = binding.editNoteTitle.getText().toString();
            String newUpdateContent = binding.editNoteContent.getText().toString();

            if(newUpdateTitle.isEmpty() || newUpdateContent.isEmpty()){
                Toast.makeText(EditNote.this,"Can't save with empty field.", Toast.LENGTH_SHORT).show();
                return;
            }

            binding.editProgressBar.setVisibility(View.VISIBLE);

            //firebase save note
            DocumentReference documentReference = db.collection("notes").document(user.getUid()).collection("myNotes").document(getIntent().getStringExtra("noteId"));
            Map<String,Object> note = new HashMap<>();
            note.put("title",newUpdateTitle);
            note.put("content",newUpdateContent);

            documentReference.update(note).addOnSuccessListener(unused -> {
                Toast.makeText(EditNote.this, "Note Saved", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }).addOnFailureListener(e -> {
                Toast.makeText(EditNote.this, "Error, Please Try Again.", Toast.LENGTH_SHORT).show();
                binding.editProgressBar.setVisibility(View.VISIBLE);
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
            Toast.makeText(this, "Cancel Note Editing.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(EditNote.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

}