package com.fitforge.domain.workouts;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Workout;
import com.fitforge.domain.Yoga;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.WorkoutPlanStrategy;

import java.util.List;

// concrete template method subclass — upper body focus.
// draws strength exercises tagged "upper" (Bench Press, Pull-up, OH Press, ...).
public class UpperBodyWorkout extends Workout {

    public UpperBodyWorkout(ExerciseFactory factory, WorkoutPlanStrategy plan) {
        super(factory, plan); // hands DI collaborators up — same pattern as every Workout subclass.
    }

    @Override
    public String getName() {
        return "Upper-Body Workout";
    }

    @Override
    protected void buildWarmup(List<Exercise> warmup, int count) { // "light" cardio to prime the body without burning out the shoulders.
        warmup.addAll(factory.drawUnique(Cardio.TYPE, "light", count));
    }

    @Override
    protected void buildMain(List<Exercise> main, int count) { // the whole point — strength exercises tagged "upper".
        main.addAll(factory.drawUnique(Strength.TYPE, "upper", count));
    }

    @Override
    protected void buildCooldown(List<Exercise> cooldown, int count) { // "mobility" yoga to stretch out the worked muscles.
        cooldown.addAll(factory.drawUnique(Yoga.TYPE, "mobility", count));
    }
}
