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

class StrategyTest {

    @Test
    void beginnerPlan_scalesIntensityDown() {
        WorkoutPlanStrategy plan = new BeginnerPlan();
        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(7, ex.getRepsPerSet());
    }

    @Test
    void advancedPlan_increasesSetsAndIntensity() {
        WorkoutPlanStrategy plan = new AdvancedPlan();
        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(4, ex.getNumberOfSets());
        assertEquals(13, ex.getRepsPerSet());
    }

    @Test
    void customPlan_respectsUserConfiguration() {
        CustomPlan plan = new CustomPlan();
        plan.configure(1, 2, 3, 2.0);

        assertEquals(1, plan.warmupExerciseCount());
        assertEquals(2, plan.mainExerciseCount());
        assertEquals(3, plan.cooldownExerciseCount());

        Strength ex = new Strength("Bench", 3, 10);
        plan.apply(List.of(ex));
        assertEquals(20, ex.getRepsPerSet());
    }

    @Test
    void plans_polymorphism_sameCallSiteDifferentBehavior() {
        List<WorkoutPlanStrategy> all = List.of(
                new BeginnerPlan(), new AdvancedPlan(), new CustomPlan());

        for (WorkoutPlanStrategy plan : all) {
            Exercise ex = new Strength("Pushups", 3, 10);
            assertDoesNotThrow(() -> plan.apply(List.of(ex)));
            assertFalse(ex.getDescription().isBlank());
        }
    }
}
