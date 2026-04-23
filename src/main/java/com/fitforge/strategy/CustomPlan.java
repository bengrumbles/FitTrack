package com.fitforge.strategy;

import com.fitforge.domain.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CustomPlan implements WorkoutPlanStrategy {
    public static final String ID = "Custom";

    private int warmup = 2;
    private int main = 4;
    private int cooldown = 2;
    private double intensity = 1.0;

    @Override public String getId() { return ID; }
    @Override public int warmupExerciseCount()   { return warmup; }
    @Override public int mainExerciseCount()     { return main; }
    @Override public int cooldownExerciseCount() { return cooldown; }

    public void configure(int warmup, int main, int cooldown, double intensity) {
        this.warmup = warmup;
        this.main = main;
        this.cooldown = cooldown;
        this.intensity = intensity;
    }

    @Override // reads its own intensity field, whatever the user configured.
    public void apply(List<Exercise> exercises) {
        for (Exercise e : exercises) {
            e.scaleIntensity(intensity);
            e.setDescription("Custom plan — user configured");
        }
    }
}
