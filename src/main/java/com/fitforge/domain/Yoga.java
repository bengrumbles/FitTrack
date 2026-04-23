package com.fitforge.domain;

public class Yoga extends Exercise {
    public static final String TYPE = "Yoga";

    private int holdSeconds;

    public Yoga(String name, int numberOfSets, int holdSeconds) {
        super(name, numberOfSets);
        this.holdSeconds = holdSeconds;
    }

    public int getHoldSeconds() {
        return holdSeconds;
    }

    public void setHoldSeconds(int holdSeconds) {
        this.holdSeconds = holdSeconds;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void scaleIntensity(double factor) {
        holdSeconds = Math.max(5, (int) Math.round(holdSeconds * factor));
    }

    @Override
    public String describe() {
        return "%s [Yoga] — %d sets x %ds hold".formatted(
                getName(), getNumberOfSets(), holdSeconds);
    }
}
