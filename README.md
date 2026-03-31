# 💪 FitForge — Workout Builder

**CSCI 4448: Object-Oriented Analysis and Design**
University of Colorado Boulder

---

## Team Members

- Ben Grumbles
- Chandler Farnsworth

---

## Development Language & Platform

- **Language:** Java (24)

---

## Project Description

FitForge is a workout planning and tracking application built in Java. Users can create, customize, and persist personalized workout plans made up of different exercise types — Strength, Cardio, and Mobility. Workouts are structured into warm-up, main, and cooldown phases, and users can adjust sets, add descriptions, and build routines through a clean interface.

The app supports saving and loading workouts so users can build a library of routines over time. A simple UI (JavaFX or Swing) allows users to interact with their workout plans without touching code.

---

## Design Patterns Used

### 1. 🏭 Factory Pattern — `ExerciseFactory`
`ExerciseFactory` centralizes creation of `Exercise` objects (`Strength`, `Cardio`, `Mobility`). Callers request an exercise by type string without knowing which concrete class gets instantiated. This codes to the `Exercise` abstraction and keeps construction logic in one place.

```java
Exercise e = factory.getNewExercise("Strength");
```

### 2. 📐 Template Method Pattern — `Workout`
`Workout` is an abstract class that defines the skeleton of a workout plan in `createWorkout()` — calling `createWarmup()`, `createMainWorkout()`, and `createCooldown()` in order. Concrete subclasses (e.g., `LegDay`, `UpperBodyWorkout`) implement each phase differently while the overall structure stays fixed.

```java
final void createWorkout() {
    createWarmup();
    createMainWorkout();
    createCooldown();
}
```

### 3. 🎨 Decorator Pattern *(planned)*
An `ExerciseDecorator` wraps any `Exercise` to add behavior dynamically — for example, a `TimedExercise` decorator that adds a rest timer, or a `LoggedExercise` that tracks completion. This avoids bloating the class hierarchy with every possible combination of features.

### 4. 👁️ Observer Pattern *(planned)*
A `WorkoutSession` will act as the subject, notifying registered observers (e.g., a `ProgressTracker`, `CalorieEstimator`) whenever an exercise is completed. This decouples session tracking from the core workout logic.

---

## OO Design Principles

| Principle | How It's Applied |
|---|---|
| **Code to abstractions** | `Exercise` is the base type used throughout; `Workout` lists hold `Exercise`, not `Strength`/`Cardio` directly |
| **Polymorphism** | `Exercise.toString()` is overridden per subclass; workout phases are filled polymorphically |
| **Dependency Injection** | `Workout` subclasses receive an `ExerciseFactory` rather than constructing exercises themselves |
| **No big switch/if-else** | Type-based branching is confined to `ExerciseFactory`; all other logic treats things as `Exercise` |
| **Open/Closed** | New exercise types or workout templates are added by subclassing, not modifying existing code |