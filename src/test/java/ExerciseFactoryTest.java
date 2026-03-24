import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseFactoryTest {
    private ExerciseFactory exerciseFactory = new ExerciseFactory();

    @Test
    public void testToStringWithFactory(){
        Exercise exercise = exerciseFactory.getNewExercise("Strength");
        assertTrue(exercise.toString().contains("Strength"));
    }

    @Test
    public void testCreateExerciseWithName(){
        Exercise exercise = exerciseFactory.getNewExercise("Cardio", "Test Name");
        assertTrue(
                exercise.toString().contains("Cardio") &&
                        exercise.toString().contains("Test Name")
        );
    }
}
