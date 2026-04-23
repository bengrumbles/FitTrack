package com.fitforge.domain;

public class Cardio extends Exercise {
    public static final String TYPE = "Cardio";

    private int durationMinutes;

    public Cardio(String name, int numberOfSets, int durationMinutes) {
        super(name, numberOfSets);
        this.durationMinutes = durationMinutes;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    @Override
    public String getType() {
        return TYPE;
    }

    @Override
    public void scaleIntensity(double factor) {
        durationMinutes = Math.max(1, (int) Math.round(durationMinutes * factor));
    }

    @Override
    public String describe() {
        return "%s [Cardio] — %d sets x %d min".formatted(
                getName(), getNumberOfSets(), durationMinutes);
    }
}
