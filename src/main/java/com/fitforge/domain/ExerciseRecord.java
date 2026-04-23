package com.fitforge.domain;

import jakarta.persistence.Embeddable;

// flat, persistable snapshot of an Exercise for workout-history storage.
// the domain Exercise hierarchy stays pure (no JPA annotations mixed in).
// when a session is saved, each live Exercise is converted into one of these flat records via from(Exercise).
// this keeps the Repository pattern cleanly separated from the polymorphic domain model.
@Embeddable // stored inline inside a WorkoutSession's child table — not a top-level entity, no id of its own.
public class ExerciseRecord {
    private String type;     // "Strength" / "Cardio" / "Yoga" — captured as a string, not an enum, so old saved rows still load if we add new types later.
    private String name;     // e.g. "Bench Press"
    private int sets;        // flat int, no matter what subclass we came from.
    private String summary;  // the polymorphic describe() output baked into a String. load-time needs zero domain logic to display it.
    private String cues;     // the coaching one-liner from the factory catalog.

    public ExerciseRecord() {}  // required by JPA — Hibernate uses the no-arg ctor + setters to rehydrate a row.

    public ExerciseRecord(String type, String name, int sets, String summary, String cues) {
        this.type = type;
        this.name = name;
        this.sets = sets;
        this.summary = summary;
        this.cues = cues;
    }

    // the conversion boundary — domain object in, flat persistence snapshot out.
    // called by WorkoutService.saveWorkout via .map(ExerciseRecord::from).
    // note that e.describe() is called HERE so the polymorphic output is resolved at save time.
    public static ExerciseRecord from(Exercise e) {
        return new ExerciseRecord(
                e.getType(), e.getName(), e.getNumberOfSets(), e.describe(), e.getCues());
    }

    // getters — used by Thymeleaf via ${rec.name}, ${rec.summary}, etc.
    public String getType()    { return type; }
    public String getName()    { return name; }
    public int getSets()       { return sets; }
    public String getSummary() { return summary; }
    public String getCues()    { return cues; }

    // setters — we never call these ourselves, but JPA/Hibernate needs them for reflection-based hydration when it loads a row.
    public void setType(String type)       { this.type = type; }
    public void setName(String name)       { this.name = name; }
    public void setSets(int sets)          { this.sets = sets; }
    public void setSummary(String summary) { this.summary = summary; }
    public void setCues(String cues)       { this.cues = cues; }
}
