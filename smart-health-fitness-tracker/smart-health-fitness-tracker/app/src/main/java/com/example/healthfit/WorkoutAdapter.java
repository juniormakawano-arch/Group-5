package com.example.healthfit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class WorkoutAdapter extends RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder> {

    private List<Workout> workoutList;

    public WorkoutAdapter(List<Workout> workoutList) {
        this.workoutList = workoutList;
    }

    @NonNull
    @Override
    public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_workout_list, parent, false);
        return new WorkoutViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkoutViewHolder holder, int position) {
        Workout workout = workoutList.get(position);
        holder.name.setText(workout.getName());
        holder.details.setText(workout.getDuration() + " • " + workout.getDifficulty());
        holder.icon.setImageResource(workout.getImageResId());

        holder.btnStart.setOnClickListener(v ->
                Toast.makeText(v.getContext(), "Starting " + workout.getName() + " session...", Toast.LENGTH_SHORT).show()
        );
    }

    @Override
    public int getItemCount() {
        return workoutList.size();
    }

    public static class WorkoutViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;
        ImageView icon;
        Button btnStart;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvWorkoutName);
            details = itemView.findViewById(R.id.tvWorkoutDetails);
            icon = itemView.findViewById(R.id.ivWorkoutIcon);
            btnStart = itemView.findViewById(R.id.btnStart);
        }
    }
}