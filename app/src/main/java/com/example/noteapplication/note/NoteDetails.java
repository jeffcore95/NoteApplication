package com.example.noteapplication.note;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.noteapplication.MainActivity;
import com.example.noteapplication.R;
import com.example.noteapplication.databinding.ActivityNoteDetailsBinding;

import java.util.Objects;

public class NoteDetails extends AppCompatActivity {


    ActivityNoteDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        binding.contentNoteDetails.noteDetailsContent.setMovementMethod(new ScrollingMovementMethod());

        binding.contentNoteDetails.noteDetailsContent.setText(getIntent().getStringExtra("content"));
        binding.noteDetailsTitle.setText(getIntent().getStringExtra("title"));
        binding.contentNoteDetails.noteDetailsContent.setBackgroundColor(getResources().getColor(getIntent().getIntExtra("color",0)));


        binding.fab.setOnClickListener(view -> {

            Intent intent = new Intent(view.getContext(),EditNote.class);
            intent.putExtra("title",getIntent().getStringExtra("title"));
            intent.putExtra("content",getIntent().getStringExtra("content"));
            intent.putExtra("noteId",getIntent().getStringExtra("noteId"));
            startActivity(intent);
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
            startActivity(new Intent(NoteDetails.this, MainActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }


}