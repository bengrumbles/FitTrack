package com.fitforge.service;

import com.fitforge.domain.Exercise;
import com.fitforge.domain.ExerciseRecord;
import com.fitforge.domain.Workout;
import com.fitforge.domain.WorkoutSession;
import com.fitforge.domain.workouts.CoreWorkout;
import com.fitforge.domain.workouts.FullBodyWorkout;
import com.fitforge.domain.workouts.LowerBodyWorkout;
import com.fitforge.domain.workouts.UpperBodyWorkout;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.repository.WorkoutSessionRepository;
import com.fitforge.strategy.WorkoutPlanStrategy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

// the orchestrator — where all four design patterns shake hands.
//   Factory         -> injected as `factory`, passed down into every Workout.
//   Strategy        -> injected as a List<WorkoutPlanStrategy>, keyed by id in `strategiesById`.
//   Template Method -> `workout.build()` below triggers the buildWarmup/Main/Cooldown skeleton.
//   Repository      -> `repository.save(session)` persists the finished workout.
// the controller is thin on purpose — almost every decision lives here.
@Service // single spring-managed bean; injected into WorkoutController.
public class WorkoutService {

    private final ExerciseFactory factory;                           // pattern #1 — handed to every Workout we build.
    private final WorkoutSessionRepository repository;               // pattern #4 — the JPA seam.
    private final Map<String, WorkoutPlanStrategy> strategiesById;   // pattern #2 — lookup table "beginner" -> BeginnerPlan, etc.

    // mini-factory for the Workout subclasses themselves.
    // each value is a BiFunction pointing at a two-arg constructor (e.g. FullBodyWorkout::new).
    // lets buildWorkout() pick the right subclass by string name without a big switch statement.
    // adding a new workout type = add one line here.
    private final Map<String, BiFunction<ExerciseFactory, WorkoutPlanStrategy, Workout>> workoutBuilders =
            Map.of(
                    "Full-Body",  FullBodyWorkout::new,
                    "Upper-Body", UpperBodyWorkout::new,
                    "Lower-Body", LowerBodyWorkout::new,
                    "Core",       CoreWorkout::new
            );

    // constructor-based DI. spring auto-wires:
    //   - the single ExerciseFactory bean
    //   - the single repository proxy
    //   - a List of EVERY bean implementing WorkoutPlanStrategy (BeginnerPlan, AdvancedPlan, CustomPlan).
    // we then collapse that list into a map keyed by each strategy's getId() so the UI can pick by name.
    // open/closed: add a new @Component plan tomorrow and nothing in this constructor changes.
    public WorkoutService(ExerciseFactory factory,
                          WorkoutSessionRepository repository,
                          List<WorkoutPlanStrategy> strategies) {
        this.factory = factory;
        this.repository = repository;
        this.strategiesById = strategies.stream()
                .collect(Collectors.toMap(WorkoutPlanStrategy::getId, s -> s)); // "beginner" -> BeginnerPlan, etc.
    }

    // fixed display order for the UI — maps the order we want buttons to appear in, independent of map iteration order.
    private static final List<String> TEMPLATE_ORDER =
            List.of("Full-Body", "Upper-Body", "Lower-Body", "Core");
    private static final List<String> PLAN_ORDER =
            List.of("Beginner", "Advanced", "Custom");

    // filter the display order down to what's actually registered. if a template/plan is removed, the UI silently drops it.
    public List<String> availableWorkoutTemplates() {
        return TEMPLATE_ORDER.stream()
                .filter(workoutBuilders::containsKey) // only keep names we can actually build.
                .toList();
    }

    public List<String> availablePlans() {
        return PLAN_ORDER.stream()
                .filter(strategiesById::containsKey) // same idea — only show plans we have a strategy bean for.
                .toList();
    }

    // the money method — builds a concrete Workout of the chosen type, wired with the chosen strategy.
    // controller calls this when the user picks a workout + plan from the form.
    public Workout buildWorkout(String templateName, String planId) {
        WorkoutPlanStrategy plan = strategiesById.get(planId); // strategy lookup.
        if (plan == null) {
            throw new IllegalArgumentException("Unknown plan: " + planId); // clear error if the form posts a bad id.
        }
        BiFunction<ExerciseFactory, WorkoutPlanStrategy, Workout> builder =
                workoutBuilders.get(templateName);                         // workout-subclass lookup.
        if (builder == null) {
            throw new IllegalArgumentException("Unknown workout template: " + templateName);
        }

        Workout workout = builder.apply(factory, plan); // runs e.g. new UpperBodyWorkout(factory, plan).
        workout.build();                                // TEMPLATE METHOD — fills warmup/main/cooldown via the subclass hooks.
        return workout;
    }

    // persistence path — build a workout, flatten its exercises, save the session.
    public WorkoutSession saveWorkout(String templateName, String planId) {
        Workout workout = buildWorkout(templateName, planId);
        List<ExerciseRecord> records = workout.allExercises().stream()
                .map(ExerciseRecord::from) // CONVERSION BOUNDARY — polymorphic Exercise -> flat ExerciseRecord. describe() gets resolved here.
                .toList();
        WorkoutSession session = new WorkoutSession(
                workout.getName(), planId, records);
        return repository.save(session); // returns the managed entity with a populated id.
    }

    // history-page query — newest first. delegates straight to the repo.
    public List<WorkoutSession> history() {
        return repository.findAllByOrderByCompletedAtDesc();
    }

    public long historyCount() {
        return repository.count(); // cheap SELECT COUNT(*) — used in the UI header.
    }

    public void clearHistory() {
        repository.deleteAll(); // wipe the table — used by the "clear history" button.
    }

    // tiny pass-through so callers don't have to know about Workout's internals — just hand us a workout and we'll flatten it.
    public List<Exercise> allExercisesOf(Workout workout) {
        return workout.allExercises();
    }
}
