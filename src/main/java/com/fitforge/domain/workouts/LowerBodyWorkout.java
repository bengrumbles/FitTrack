package com.fitforge.domain.workouts;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Workout;
import com.fitforge.domain.Yoga;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.WorkoutPlanStrategy;

import java.util.List;

// concrete template method subclass — lower body focus.
// structurally identical to UpperBodyWorkout, only the strength focus tag differs ("lower" vs "upper").
// that's the pattern working: changing the output = changing one string.
public class LowerBodyWorkout extends Workout {

    public LowerBodyWorkout(ExerciseFactory factory, WorkoutPlanStrategy plan) {
        super(factory, plan);
    }

    @Override
    public String getName() {
        return "Lower-Body Workout";
    }

    @Override
    protected void buildWarmup(List<Exercise> warmup, int count) { // "light" cardio to get blood into the legs without pre-fatiguing them.
        warmup.addAll(factory.drawUnique(Cardio.TYPE, "light", count));
    }

    @Override
    protected void buildMain(List<Exercise> main, int count) { // strength tagged "lower" — Back Squat, Deadlift, Walking Lunge, Hip Thrust, etc.
        main.addAll(factory.drawUnique(Strength.TYPE, "lower", count));
    }

    @Override
    protected void buildCooldown(List<Exercise> cooldown, int count) { // "mobility" yoga — Pigeon, Warrior II, Downward Dog to stretch hips and hamstrings.
        cooldown.addAll(factory.drawUnique(Yoga.TYPE, "mobility", count));
    }
}
