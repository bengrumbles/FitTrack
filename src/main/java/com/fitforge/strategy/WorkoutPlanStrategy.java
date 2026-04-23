package com.fitforge.strategy; // design pattern #2: strategy
// the stategy pattern lets me swap out an algorithm at runtime without the caller knowing which algorithm it's running
// the "algorithm" is how a workput plan shapes a session - how many warmup/main/cooldown exercises, and how intensely to scale them.

import com.fitforge.domain.Exercise;

import java.util.List;

public interface WorkoutPlanStrategy { // an interface is a pure contract
    // class can implement multiple interfaces but only extend one class

    // two purposes: 
    // 1. tell the workout how many exercises to draw per phase - warmupExerciseCount(), mainExerciseCount(), cooldownExerciseCount().
    // 2. post-process the finished exercise list - apply(List<Exercise> exercises). This is where the intensity scaling happens.

    String getId(); // returns "Beginner", "Advanced", "Custom". Used by WorkoutService to build a Map.

    int mainExerciseCount();

    int warmupExerciseCount();

    int cooldownExerciseCount();

    void apply(List<Exercise> exercises); // is polymorphic. First: the caller (Workout.build()) holds a WorkoutPlanStrategy reference and calls plan.apply(...) without caring which plan it is.
    // second: each plan loops over Exercise references and calls e.scaleIntensity(factor) without caring whether each one is Strength, Cardio, Yoga, etc.
}
