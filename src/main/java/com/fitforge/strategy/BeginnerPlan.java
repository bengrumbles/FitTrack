package com.fitforge.strategy;

import com.fitforge.domain.Exercise;
import org.springframework.stereotype.Component;

import java.util.List;

@Component // spring annotation that marks the class as a bean. at startup spring scans the com.fitforge package
// finds every @Component and instantiates one of each. that's how WorkoutService gets its List<WorkoutPlanStategy> injected for free - spring hands it all three.
public class BeginnerPlan implements WorkoutPlanStrategy { // promises the class will provide every method the interface declared, if you forgot one, compilation fails. 
    public static final String ID = "Beginner"; // public constant that matches the string used in the UI dropdown and in the WorkourService lookup map. 

    @Override public String getId() { return ID; }
    @Override public int warmupExerciseCount()   { return 2; }
    @Override public int mainExerciseCount()     { return 3; }
    @Override public int cooldownExerciseCount() { return 1; } // pure data.

    @Override // is where each plan's personality live. beginner scales intensity down (0.7x): a bech press with 10 reps becomes 7.
    public void apply(List<Exercise> exercises) {
        for (Exercise e : exercises) {
            e.scaleIntensity(0.7);
            e.setDescription("Beginner-friendly — focus on form");
        }
    }
}
