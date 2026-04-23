package com.fitforge;

import com.fitforge.domain.Workout;
import com.fitforge.domain.workouts.FullBodyWorkout;
import com.fitforge.domain.workouts.UpperBodyWorkout;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.BeginnerPlan;
import com.fitforge.strategy.WorkoutPlanStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkoutTemplateTest {

    private final ExerciseFactory factory = new ExerciseFactory();
    private final WorkoutPlanStrategy plan = new BeginnerPlan();

    @Test
    void templateMethod_buildRunsAllThreePhasesInOrder() {
        Workout w = new FullBodyWorkout(factory, plan);
        w.build();

        assertEquals(plan.warmupExerciseCount(),   w.getWarmup().size());
        assertEquals(plan.mainExerciseCount(),     w.getMain().size());
        assertEquals(plan.cooldownExerciseCount(), w.getCooldown().size());
    }

    @Test
    void differentSubclasses_fillPhasesDifferently_withSameTemplate() {
        Workout full  = new FullBodyWorkout(factory, plan);
        Workout upper = new UpperBodyWorkout(factory, plan);
        full.build();
        upper.build();

        boolean fullHasYoga = full.getMain().stream()
                .anyMatch(e -> "Yoga".equals(e.getType()));
        boolean upperHasYoga = upper.getMain().stream()
                .anyMatch(e -> "Yoga".equals(e.getType()));

        assertTrue(fullHasYoga);
        assertFalse(upperHasYoga);
    }

    @Test
    void workout_appliesStrategyToEveryExercise() {
        Workout w = new FullBodyWorkout(factory, new BeginnerPlan());
        w.build();

        w.allExercises().forEach(e ->
                assertTrue(e.getDescription().contains("Beginner")));
    }
}
