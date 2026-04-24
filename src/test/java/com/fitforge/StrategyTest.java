package com.fitforge;

import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.strategy.AdvancedPlan;
import com.fitforge.strategy.BeginnerPlan;
import com.fitforge.strategy.CustomPlan;
import com.fitforge.strategy.WorkoutPlanStrategy;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// tests for design pattern #2: strategy.
// the first three tests verify each plan's specific behavior. the fourth proves they're interchangeable —
// THAT'S the test that actually demonstrates the pattern, not just three unrelated classes with the same method name.
class StrategyTest {

    // beginner makes things EASIER. 10 reps * 0.7 = 7. pins down the intensity scale-down.
    @Test
    void beginnerPlan_scalesIntensityDown() {
        WorkoutPlanStrategy plan = new BeginnerPlan();           // use the interface type — we're only calling apply().
        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(7, ex.getRepsPerSet());                     // 10 * 0.7 rounded = 7.
    }

    // advanced makes things HARDER in two ways: adds a set AND scales intensity up.
    // asserting both effects catches a regression that only fixes one of them.
    @Test
    void advancedPlan_increasesSetsAndIntensity() {
        WorkoutPlanStrategy plan = new AdvancedPlan();
        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(4, ex.getNumberOfSets());                   // 3 + 1.
        assertEquals(13, ex.getRepsPerSet());                    // 10 * 1.3.
    }

    // custom is the only plan with a configure() method — it's NOT on the interface (interface segregation).
    // that's why this test uses the concrete CustomPlan type instead of the interface.
    @Test
    void customPlan_respectsUserConfiguration() {
        CustomPlan plan = new CustomPlan();
        plan.configure(1, 2, 3, 2.0);                            // warmup=1, main=2, cooldown=3, intensity=2.0.

        // first half — configure() actually stored the counts.
        assertEquals(1, plan.warmupExerciseCount());
        assertEquals(2, plan.mainExerciseCount());
        assertEquals(3, plan.cooldownExerciseCount());

        // second half — the stored intensity factor gets applied to exercises.
        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(20, ex.getRepsPerSet());                    // 10 * 2.0.
    }

    // THE FLAGSHIP TEST — strategy pattern in one loop.
    // three concrete plans, one interface type, one call site (plan.apply). no instanceof, no casting.
    // this is literally how WorkoutService uses strategies at runtime.
    @Test
    void plans_polymorphism_sameCallSiteDifferentBehavior() {
        List<WorkoutPlanStrategy> all = List.of(
                new BeginnerPlan(), new AdvancedPlan(), new CustomPlan()); // treated uniformly as the interface type.

        for (WorkoutPlanStrategy plan : all) {
            Exercise ex = new Strength("Pushups", 3, 10);
            assertDoesNotThrow(() -> plan.apply(List.of(ex)));   // sanity — no plan blows up on a normal exercise.
            assertFalse(ex.getDescription().isBlank());          // and the exercise still has a meaningful description after.
        }
    }
}
