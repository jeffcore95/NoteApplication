package com.example.noteapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.noteapplication.auth.Login;
import com.example.noteapplication.auth.Register;
import com.example.noteapplication.databinding.ActivityMainBinding;
import com.example.noteapplication.databinding.NoteViewLayoutBinding;
import com.example.noteapplication.note.AddNote;
import com.example.noteapplication.note.EditNote;
import com.example.noteapplication.note.NoteDetails;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import model.Note;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ActionBarDrawerToggle toggle;
    ActivityMainBinding binding;
    NoteViewLayoutBinding noteBinding;

    FirebaseFirestore db;
    FirestoreRecyclerAdapter<Note,NoteViewHolder> noteAdapter;
    FirebaseUser user;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //get database

        db = FirebaseFirestore.getInstance();
        fAuth =FirebaseAuth.getInstance();
        user = fAuth.getCurrentUser();

        View headerView = binding.navView.getHeaderView(0);
        TextView userName = headerView.findViewById(R.id.textViewUserDisplayName);
        TextView userEmail = headerView.findViewById(R.id.textViewUserDisplayEmail);

        if (user.isAnonymous()){
            userName.setText("Temporary User");
            userEmail.setVisibility(View.GONE);
        }else{

            userName.setText(getIntent().getStringExtra("username"));
            userEmail.setText(user.getEmail());
        }


        Query query = db.collection("notes").document(user.getUid()).collection("myNotes").orderBy("title",Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<Note> allNotes = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query,Note.class).build();

        noteAdapter = new FirestoreRecyclerAdapter<Note, NoteViewHolder>(allNotes) {
            @Override
            protected void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, int position, @NonNull final Note note) {


                noteBinding.titles.setText(note.getTitle());
                noteBinding.content.setText(note.getContent());
                noteBinding.noteCard.setCardBackgroundColor(noteBinding.noteBox.getResources().getColor(note.getColor(),null));


                String docId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                noteBinding.noteBox.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), ""+note.getColor(), Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(v.getContext(), NoteDetails.class);
                    intent.putExtra("title",note.getTitle());
                    intent.putExtra("content",note.getContent());
                    intent.putExtra("color",note.getColor());
                    intent.putExtra("noteId",docId);
                    v.getContext().startActivity(intent);
                });

                noteBinding.menuIcon.setOnClickListener(v -> {

                    String popUpSelectedNoteId = noteAdapter.getSnapshots().getSnapshot(position).getId();
                    PopupMenu menu = new PopupMenu(v.getContext(),v);
                    menu.setGravity(Gravity.END);

                    menu.getMenu().add("Edit").setOnMenuItemClickListener(item -> {
                        Intent intent = new Intent(v.getContext(), EditNote.class);
                        intent.putExtra("title",note.getTitle());
                        intent.putExtra("content",note.getContent());
                        intent.putExtra("noteId",popUpSelectedNoteId);
                        startActivity(intent);
                        return false;
                    });
                    menu.getMenu().add("Delete").setOnMenuItemClickListener(item -> {

                        DocumentReference documentReference = db.collection("notes").document(user.getUid()).collection("myNotes").document(popUpSelectedNoteId);
                        documentReference.delete().addOnSuccessListener(unused -> {
                            //note is successfully delete
                            Toast.makeText(MainActivity.this, "Note deleted.", Toast.LENGTH_SHORT).show();

                            startActivity(getIntent());
                            overridePendingTransition( 0, 0);

                        }).addOnFailureListener(e -> {
                            //note is fail to delete
                            Toast.makeText(MainActivity.this, "Note is fail to delete", Toast.LENGTH_SHORT).show();

                        });
                        return false;

                    });
                    menu.show();

                });


            }

            @NonNull
            @Override
            public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                noteBinding = NoteViewLayoutBinding.inflate(inflater,parent,false);

                return new NoteViewHolder(noteBinding);
            }
        };

        setSupportActionBar(binding.contentMain.toolbar);
        binding.navView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, binding.drawer,binding.contentMain.toolbar,R.string.open,R.string.close);
        binding.drawer.addDrawerListener(toggle);
        toggle.setDrawerIndicatorEnabled(true);
        toggle.syncState();

        binding.contentMain.noteList.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        binding.contentMain.noteList.setAdapter(noteAdapter);
        binding.contentMain.addNoteFloat.setOnClickListener(v -> startActivity(new Intent(v.getContext(), AddNote.class)));





    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        binding.drawer.closeDrawer(GravityCompat.START);
        switch (item.getItemId()){
            case R.id.notes:
                break;
            case R.id.addNotes:
                startActivity(new Intent(this, AddNote.class));
                break;
            case R.id.sync:
                if (user.isAnonymous()){
                    startActivity(new Intent(this, Login.class));
                }else{
                    Toast.makeText(MainActivity.this,"You are connected.", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.rating:
                break;
            case R.id.shareApp:
                break;
            case R.id.logout:
                checkUser();
                break;
            default:
                Toast.makeText(this, "Coming soon", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private void checkUser() {
        //if user is real or not
        if(user.isAnonymous()){
                displayAlert();
        }else{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(),Splash.class));
            finish();
        }
    }

    private void displayAlert() {
        AlertDialog.Builder warning = new AlertDialog.Builder(this)
                .setTitle("Are You Sure?")
                .setMessage("You are logged in with Temporary Account, logout now will delete all the note.")
                .setPositiveButton("Sync Note", (dialog, which) -> {
                    startActivity(new Intent(getApplicationContext(), Register.class));
                    finish();
                }).setNegativeButton("Logout", (dialog, which) -> {
                    user.delete().addOnSuccessListener(unused -> {
                        startActivity(new Intent(getApplicationContext(),Splash.class));
                        Toast.makeText(getApplicationContext(), "Temporary Account Deleted.", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                });

        warning.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.setting){
            Toast.makeText(this, "Setting menu under Construction.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder{
        public NoteViewHolder(@NonNull NoteViewLayoutBinding itemView) {
            super(itemView.getRoot());

        }
    }


    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noteAdapter!=null){
            noteAdapter.stopListening();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //startActivity(getIntent());
    }


}