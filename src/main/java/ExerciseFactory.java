public class ExerciseFactory {
    private int nameIndex;

    public ExerciseFactory(){
        this.nameIndex = 0;
    }

    public Exercise getNewExercise(String exerciseType) {
        nameIndex++;
        switch (exerciseType) {
            case "Strength":
                return new Strength("Strength Exercise " + nameIndex, 0);

            case "Mobility":
                return new Mobility("Mobility Exercise " + nameIndex, 0);

            case "Cardio":
                return new Cardio("Cardio Exercise " + nameIndex, 0);

            default:
                throw new IllegalArgumentException("Unknown exercise type: " + exerciseType);
        }
    }

    public Exercise getNewExercise(String exerciseType, String name) {
        switch (exerciseType) {
            case "Strength":
                return new Strength(name, 0);

            case "Mobility":
                return new Mobility(name, 0);

            case "Cardio":
                return new Cardio(name, 0);

            default:
                throw new IllegalArgumentException("Unknown exercise type: " + exerciseType);
        }

    }
}
