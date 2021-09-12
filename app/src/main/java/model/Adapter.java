package model;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapplication.note.NoteDetails;
import com.example.noteapplication.R;
import com.example.noteapplication.databinding.NoteViewLayoutBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

////////////////////////////This Adapter.java show no purpose , only review the code////////////////////////////
public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {
    NoteViewLayoutBinding binding;
    List<String> titles;
    List<String> content;

    public Adapter(List<String> titles, List<String> content){
        this.titles = titles;
        this.content = content;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        binding = NoteViewLayoutBinding.inflate(inflater,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        binding.titles.setText(titles.get(position));
        binding.content.setText(content.get(position));
        final int code = getRandomColor();
        binding.noteCard.setCardBackgroundColor(binding.noteBox.getResources().getColor(code,null));

        binding.noteBox.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NoteDetails.class);
            intent.putExtra("title",titles.get(position));
            intent.putExtra("content",content.get(position));
            intent.putExtra("color",code);
            v.getContext().startActivity(intent);
        });

    }

    private int getRandomColor() {
        List<Integer> colorCode = new ArrayList<>();
        colorCode.add(R.color.blue);
        colorCode.add(R.color.yellow);
        colorCode.add(R.color.skyBlue);
        colorCode.add(R.color.lightPurple);
        colorCode.add(R.color.lightGreen);
        colorCode.add(R.color.gray);
        colorCode.add(R.color.pink);
        colorCode.add(R.color.red);
        colorCode.add(R.color.greenLight);
        colorCode.add(R.color.notGreen);

        Random randomColor = new Random();
        int number = randomColor.nextInt(colorCode.size());
        return colorCode.get(number);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public ViewHolder(@NonNull NoteViewLayoutBinding noteViewLayoutBinding) {
            super(noteViewLayoutBinding.getRoot());
        }
    }
}
