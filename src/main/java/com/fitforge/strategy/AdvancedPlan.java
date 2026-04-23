package com.fitforge.strategy;

import com.fitforge.domain.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AdvancedPlan implements WorkoutPlanStrategy {
    public static final String ID = "Advanced";

    @Override public String getId() { return ID; }
    @Override public int warmupExerciseCount()   { return 3; }
    @Override public int mainExerciseCount()     { return 6; }
    @Override public int cooldownExerciseCount() { return 2; }

    @Override // does two things: increaseNumberofSets () (one extra set across the board) and scale intensity (1.3) harder reps, longer duration.
    public void apply(List<Exercise> exercises) {
        for (Exercise e : exercises) {
            e.increaseNumberOfSets();
            e.scaleIntensity(1.3);
            e.setDescription("Push hard — maintain intensity");
        }
    }
}
