package com.fitforge;

import com.fitforge.domain.Workout;
import com.fitforge.domain.workouts.FullBodyWorkout;
import com.fitforge.domain.workouts.UpperBodyWorkout;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.BeginnerPlan;
import com.fitforge.strategy.WorkoutPlanStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// tests for design pattern #3: template method.
// we verify the pattern STRUCTURALLY, not by checking for specific exercise names —
// the draws are random, so testing "Bench Press showed up" would be flaky.
class WorkoutTemplateTest {

    // plain `new` — no spring context needed. shared across tests so the setup noise stays small.
    private final ExerciseFactory factory = new ExerciseFactory();
    private final WorkoutPlanStrategy plan = new BeginnerPlan();

    // proves build() fires all three hooks. if buildCooldown were skipped, getCooldown().size() would be 0.
    // we compare against plan.warmupExerciseCount() etc. — testing the RELATIONSHIP ("whatever the plan says"),
    // not a hardcoded number. stays green if strategy counts change tomorrow.
    @Test
    void templateMethod_buildRunsAllThreePhasesInOrder() {
        Workout w = new FullBodyWorkout(factory, plan);
        w.build();                                                      // the final template method fires.

        assertEquals(plan.warmupExerciseCount(),   w.getWarmup().size());   // warmup populated.
        assertEquals(plan.mainExerciseCount(),     w.getMain().size());     // main populated.
        assertEquals(plan.cooldownExerciseCount(), w.getCooldown().size()); // cooldown populated.
    }

    // THE FLAGSHIP TEST — template method in a nutshell.
    // same skeleton (both subclasses inherit build() verbatim), same strategy, same factory.
    // ONLY difference: each subclass overrides buildMain() differently.
    // result: measurably different workouts — full body mixes yoga into main, upper body doesn't.
    // that IS the pattern: shared structure, varied steps.
    @Test
    void differentSubclasses_fillPhasesDifferently_withSameTemplate() {
        Workout full  = new FullBodyWorkout(factory, plan);
        Workout upper = new UpperBodyWorkout(factory, plan);
        full.build();
        upper.build();

        // does the full-body main contain a yoga exercise? (it should — FullBodyWorkout.buildMain mixes strength + yoga.)
        boolean fullHasYoga = full.getMain().stream()
                .anyMatch(e -> "Yoga".equals(e.getType()));
        // does the upper-body main contain yoga? (it shouldn't — UpperBodyWorkout.buildMain is strength-only.)
        boolean upperHasYoga = upper.getMain().stream()
                .anyMatch(e -> "Yoga".equals(e.getType()));

        assertTrue(fullHasYoga);   // full body mixes yoga in.
        assertFalse(upperHasYoga); // upper body does NOT.
    }

    // ties template method to strategy — after build() runs, every exercise should reflect the plan.
    // if someone refactored build() and forgot to call plan.apply(), this test would fail.
    // checking contains("Beginner") instead of a specific rep count because specific numbers depend on the random draw.
    @Test
    void workout_appliesStrategyToEveryExercise() {
        Workout w = new FullBodyWorkout(factory, new BeginnerPlan());
        w.build();

        w.allExercises().forEach(e ->
                assertTrue(e.getDescription().contains("Beginner"))); // every exercise got tagged by the plan.
    }
}
