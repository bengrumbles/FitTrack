package com.fitforge.domain.workouts;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Workout;
import com.fitforge.domain.Yoga;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.WorkoutPlanStrategy;

import java.util.List;

// concrete template method subclass — core focus.
// the most interesting of the four: uses a "power" cardio warmup (Burpees, Mountain Climbers)
// instead of "light" like Upper/Lower, and mixes core-tagged strength with core-tagged yoga in the main block.
public class CoreWorkout extends Workout {

    public CoreWorkout(ExerciseFactory factory, WorkoutPlanStrategy plan) {
        super(factory, plan);
    }

    @Override
    public String getName() {
        return "Core Workout";
    }

    @Override
    protected void buildWarmup(List<Exercise> warmup, int count) { // "power" cardio — high-intensity movement to prime the abs (Burpees, Jumping Jacks, etc.).
        warmup.addAll(factory.drawUnique(Cardio.TYPE, "power", count));
    }

    @Override
    protected void buildMain(List<Exercise> main, int count) {
        // roughly half core-tagged strength + half core-tagged yoga.
        int strengthCount = Math.max(1, count / 2); // floor at 1 so tiny counts still get at least one strength move.
        int yogaCount = Math.max(0, count - strengthCount);
        main.addAll(factory.drawUnique(Strength.TYPE, "core", strengthCount)); // Russian Twist, Hanging Leg Raise, Cable Crunch.
        if (yogaCount > 0) main.addAll(factory.drawUnique(Yoga.TYPE, "core", yogaCount)); // Plank, Side Plank, Boat Pose, Hollow Hold. skip if split left zero.
    }

    @Override
    protected void buildCooldown(List<Exercise> cooldown, int count) { // "mobility" yoga — chill stretches after all that ab work.
        cooldown.addAll(factory.drawUnique(Yoga.TYPE, "mobility", count));
    }
}
