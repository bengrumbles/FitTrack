package com.fitforge.domain;

public class Strength extends Exercise { // subclass of the abstract Exercise class
    public static final String TYPE = "Strength"; // a public constant so other classes can refer to the type name without typing the raw string

    private int repsPerSet; // the field unique to Strength

    public Strength(String name, int numberOfSets, int repsPerSet) {
        super(name, numberOfSets);
        this.repsPerSet = repsPerSet;
    }

    public int getRepsPerSet() {
        return repsPerSet;
    }

    public void setRepsPerSet(int repsPerSet) {
        this.repsPerSet = repsPerSet;
    }

    @Override // returns the string identifier, every subclass is forced to answer "what type am i" no switch statement needed
    public String getType() {
        return TYPE;
    }

    @Override // strategy like beginner plan holds a list and calls scale intensity to each one. no idea whether its holding strength, cardio, or yoga. 
    public void scaleIntensity(double factor) {
        repsPerSet = Math.max(1, (int) Math.round(repsPerSet * factor)); // floor at 1 so scaling by 0.1 doesn't give you 0 reps
    }

    @Override // each type prints itself in a type-specific way, format: "3 sets x 10 reps"
    public String describe() {
        return "%s [Strength] — %d sets x %d reps".formatted(
                getName(), getNumberOfSets(), repsPerSet);
    }
}
