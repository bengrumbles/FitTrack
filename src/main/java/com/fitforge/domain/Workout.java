package com.fitforge.domain; // design pattern #3: template method.
// pattern says: fix the overall algorithm in a final method on the base class
// but let subclasses fill in specific steps.

import com.fitforge.factory.ExerciseFactory;
import com.fitforge.strategy.WorkoutPlanStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public abstract class Workout { // abstract because Workout is a concept, only the concrete subclasses (FullBody, Upper, Lower, Core) are instantiable.
    protected final ExerciseFactory factory; // injected via constructor (DI). final so it never swaps out once built. protected so subclasses can call factory.drawUnique().
    protected final WorkoutPlanStrategy plan; // the strategy that tells this workout how many exercises per phase + how to post-process them.

    private final List<Exercise> warmup = new ArrayList<>();   // private state of the in-progress workout.
    private final List<Exercise> main = new ArrayList<>();     // subclasses can't see these directly; they receive them as a
    private final List<Exercise> cooldown = new ArrayList<>(); // parameter in the buildXxx hooks.

    protected Workout(ExerciseFactory factory, WorkoutPlanStrategy plan) { // constructor-based dependency injection. the workout doesn't new its own factory or strategy, it's handed them.
        this.factory = factory;
        this.plan = plan;
    }

    public final void build() { // this is the template method. final means subclasses cannot override it.
        buildWarmup(warmup, plan.warmupExerciseCount()); // first — ask the strategy how many, then delegate to the subclass to pick which.
        buildMain(main, plan.mainExerciseCount());       // second — same thing for main.
        buildCooldown(cooldown, plan.cooldownExerciseCount()); // final — same thing for cooldown.
        plan.apply(allExercises()); // hand the finished list to the strategy so it can post-process (scale intensity, set description).
    } // subclasses can change what goes in each phase but never the phase order. this is the whole point of the pattern: enforce a consistent high-level flow.

    protected abstract void buildWarmup(List<Exercise> warmup, int count); // methods are the "hooks" the base class doesn't know what exercises belong in a warmup for a lower-body
    protected abstract void buildMain(List<Exercise> main, int count); // workout versus a core workout, only the subclass does. so it says "fill this list with count exercises"
    protected abstract void buildCooldown(List<Exercise> cooldown, int count); // and leaves the how to the child. mandatory because they're abstract.

    public abstract String getName(); // subclass announces its own display name ("Full-Body Workout", "Upper-Body Workout", etc.) for the UI + saved history.

    // getters return an unmodifiable COPY so callers (controller, Thymeleaf) can't mutate the workout's internal state.
    public List<Exercise> getWarmup()   { return List.copyOf(warmup); }
    public List<Exercise> getMain()     { return List.copyOf(main); }
    public List<Exercise> getCooldown() { return List.copyOf(cooldown); }

    public List<Exercise> allExercises() { // flatten all three phases into one list — used by build() to hand everything to plan.apply() and by the service when saving to history.
        List<Exercise> all = new ArrayList<>();
        Stream.of(warmup, main, cooldown).forEach(all::addAll); // stream-flavored way to do three .addAll() calls in one line.
        return all;
    }
}
