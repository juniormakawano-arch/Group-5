package com.example.healthfit;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class WorkoutListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_list);

        RecyclerView recyclerView = findViewById(R.id.rvWorkouts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Workout> workouts = new ArrayList<>();
        // Add sample workouts with system icons for now
        workouts.add(new Workout("Morning Yoga", "20 Mins", "Beginner", "Stretch and relax", android.R.drawable.ic_menu_compass));
        workouts.add(new Workout("HIIT Cardio", "15 Mins", "Advanced", "Burn fat fast", android.R.drawable.ic_menu_mylocation));
        workouts.add(new Workout("Pushup Challenge", "10 Mins", "Intermediate", "Build chest strength", android.R.drawable.ic_menu_manage));
        workouts.add(new Workout("Plank Hold", "5 Mins", "Easy", "Core stability", android.R.drawable.ic_menu_recent_history));
        workouts.add(new Workout("Squat Set", "12 Mins", "Medium", "Lower body power", android.R.drawable.ic_menu_agenda));

        WorkoutAdapter adapter = new WorkoutAdapter(workouts);
        recyclerView.setAdapter(adapter);
    }
}