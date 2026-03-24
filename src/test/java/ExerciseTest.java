import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ExerciseTest {
    @Test
    public void testIncreaseNumberOfSets(){
        Exercise exercise = new Exercise("Test Exercise", 0);
        exercise.increaseNumberOfSets();
        assertEquals(exercise.getNumberOfSets(), 1);
    }

    @Test
    public void testDecreaseNumberOfSets(){
        Exercise exercise = new Exercise("Test Exercise", 5);
        exercise.decreaseNumberOfSets();
        assertEquals(exercise.getNumberOfSets(), 4);
    }

    @Test
    public void testExerciseDescription(){
        Exercise exercise = new Exercise("Pushups", 2);
        exercise.setExerciseDescription("You must push up!");
        assertTrue(exercise.getExerciseDescription().equals("You must push up!"));
    }
}
