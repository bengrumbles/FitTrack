package com.fitforge.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// the aggregate root for workout history — one row per finished workout.
// this is what the Repository pattern actually saves and loads.
// the live Workout object is transient; once a user hits "save", we flatten
// its exercises into ExerciseRecords and stuff them inside a WorkoutSession.
@Entity // top-level JPA entity — gets its own table (unlike ExerciseRecord which is @Embeddable).
@Table(name = "workout_session") // explicit table name so the schema is predictable across DBs.
public class WorkoutSession {

    @Id // primary key.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB auto-increments — id is null until save() returns.
    private Long id;

    private String workoutName;    // "Upper-Body Workout", "Core Workout", etc. — comes from Workout.getName().
    private String planId;         // "beginner" / "advanced" / "custom" — so we remember which strategy built this.
    private Instant completedAt;   // UTC timestamp stamped at save time, not request time.

    @ElementCollection(fetch = FetchType.EAGER) // not a separate entity — a collection of @Embeddable values. eager so Thymeleaf can read exercises after the tx closes (avoids LazyInitializationException).
    @CollectionTable(                            // child table that holds the flattened exercise rows.
            name = "session_exercise",           // table name for the child rows.
            joinColumns = @JoinColumn(name = "session_id")) // FK column back to workout_session.id.
    private List<ExerciseRecord> exercises = new ArrayList<>(); // initialized so it's never null, even on a fresh unsaved session.

    public WorkoutSession() {} // required by JPA — Hibernate uses the no-arg ctor + setters to rehydrate a row.

    // the "real" constructor called by WorkoutService.saveWorkout after mapping Exercise -> ExerciseRecord.
    public WorkoutSession(String workoutName, String planId, List<ExerciseRecord> exercises) {
        this.workoutName = workoutName;
        this.planId = planId;
        this.exercises = new ArrayList<>(exercises); // defensive copy — caller can't mutate our internal list later.
        this.completedAt = Instant.now();            // stamp the time here, not in the controller, so it reflects when the save happened.
    }

    // getters — used by Thymeleaf (${session.workoutName}, ${session.exercises}, etc.) and by tests.
    public Long getId()                      { return id; }
    public String getWorkoutName()           { return workoutName; }
    public String getPlanId()                { return planId; }
    public Instant getCompletedAt()          { return completedAt; }
    public List<ExerciseRecord> getExercises() { return exercises; }

    public int exerciseCount() { return exercises.size(); } // small convenience for the history page — "12 exercises" without iterating in the template.
}
