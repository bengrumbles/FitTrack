package com.fitforge.domain.workouts;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Workout;
import com.fitforge.domain.Yoga;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.WorkoutPlanStrategy;

import java.util.List;

// concrete template method subclass — the "balanced" workout.
// mixes strength + yoga in the main block so it hits a bit of everything.
public class FullBodyWorkout extends Workout {

    public FullBodyWorkout(ExerciseFactory factory, WorkoutPlanStrategy plan) {
        super(factory, plan); // hands the injected factory + strategy up to the base. this subclass never news its own collaborators.
    }

    @Override
    public String getName() { // display name for the UI + the saved WorkoutSession.workoutName column.
        return "Full-Body Workout";
    }

    @Override
    protected void buildWarmup(List<Exercise> warmup, int count) { // cardio warmup — any focus, just get the heart rate up.
        warmup.addAll(factory.drawUnique(Cardio.TYPE, count));
    }

    @Override
    protected void buildMain(List<Exercise> main, int count) {
        // roughly 2/3 strength + 1/3 yoga for variety.
        int strengthCount = Math.max(1, (count * 2) / 3); // floor at 1 so tiny counts (like Beginner's 3) still get at least one strength move.
        int yogaCount = Math.max(0, count - strengthCount);
        main.addAll(factory.drawUnique(Strength.TYPE, strengthCount));
        if (yogaCount > 0) main.addAll(factory.drawUnique(Yoga.TYPE, yogaCount)); // skip the yoga draw if the split left zero — prevents an empty call.
    }

    @Override
    protected void buildCooldown(List<Exercise> cooldown, int count) { // yoga cooldown — any focus, just something chill to end on.
        cooldown.addAll(factory.drawUnique(Yoga.TYPE, count));
    }
}
