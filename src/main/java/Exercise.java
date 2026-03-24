public class Exercise {
    private String name;
    private int numberOfSets;
    private String experienceLevel;
    private String exerciseDescription;

    public Exercise(String name, int numberOfSets) {
        this.name = name;
        this.numberOfSets = numberOfSets;
    }

    public void increaseNumberOfSets(){
        this.numberOfSets++;
    }

    public void decreaseNumberOfSets(){
        this.numberOfSets--;
    }

    public void setExerciseDescription(String description){
        this.exerciseDescription = description;
    }

    @Override
    public String toString(){
        return "Exercise: " + name + "\n" +
                "Exercise Type: " + this.getClass().getSimpleName() + "\n" +
                "Number of sets: " + numberOfSets + "\n";
    }


}
