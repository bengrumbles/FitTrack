package com.fitforge.domain;

public abstract class Exercise { // the abstract base, cannot do new. its a concept, only its subclasses are instantiable. this is what forces every real exercise to pick a concrete type.
    private final String name; // shared fields, every exercise has regardless of type.
    private int numberOfSets;
    private String description = "";
    private String cues = "";

    protected Exercise(String name, int numberOfSets) { // constructor, only subclases should call it.
        this.name = name;
        this.numberOfSets = numberOfSets;
    }

    public String getName() { // an exercise name doesnt change after creation.
        return name;
    }

    public int getNumberOfSets() {
        return numberOfSets;
    }

    public void setNumberOfSets(int numberOfSets) {
        this.numberOfSets = numberOfSets;
    }

    public void increaseNumberOfSets() { // methods the advanced strategy uses. 
        numberOfSets++;
    }

    public void decreaseNumberOfSets() { // the floor at zero guard in decrement prevents negative sets.
        if (numberOfSets > 0) numberOfSets--;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) { // is set by strategy (e.g. "Beginner - friendly - focus on form")
        this.description = description == null ? "" : description;
    }

    // cues and description are seperate because strategy can overwrite its description without stomping on the coaching cue.
    public String getCues() { // is set by factory from the catalog (e.g. "Retract shoulder blades; bar to mid-chest")
        return cues;
    }

    public void setCues(String cues) {
        this.cues = cues == null ? "" : cues;
    }

    public abstract String getType(); // no method body, forces every subclass to implement it. if strength forgot to override it, the code wouldn't compile.

    public abstract void scaleIntensity(double factor); // base class declares a hook, each subclass fills in what "scale intensity" means fo its own data. 

    public String describe() { // default implementation, subclasses inherit a default ("Bench Press [Strength] - 3 sets (Beginner-friendly)") but can override to add type-specific data. Difference between abstract methods and virtual methods.
        return "%s [%s] — %d sets%s".formatted(
                name,
                getType(),
                numberOfSets,
                description.isBlank() ? "" : " (" + description + ")"
        );
    }

    @Override
    public String toString() { // so that printing in exercise is a log.
        return describe();
    }
}
