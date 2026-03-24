import java.util.ArrayList;
import java.util.List;

public abstract class Workout {
    public List<Exercise> warmups = new ArrayList<>();
    public List<Exercise> mainExercises = new ArrayList<>();
    public List<Exercise> cooldowns = new ArrayList<>();

    final void createWorkout(){
        createWarmup();
        createMainWorkout();
        createCooldown();
    }

    abstract void createWarmup();
    abstract void createMainWorkout();
    abstract void createCooldown();
}
