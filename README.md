# FitForge — Workout Builder

**CSCI 4448: Object-Oriented Analysis and Design**
University of Colorado Boulder

---

## Team Members

- Ben Grumbles
- Chandler Farnsworth

---

## Development Language & Platform

- **Language:** Java 21
- **Framework:** Spring Boot 3.3.5 (Web + Data JPA + Thymeleaf)
- **Build:** Gradle (Kotlin DSL)
- **Database:** H2 in-memory (swap for Postgres/MySQL with a config change)

---

## Project Description

FitForge is a Spring Boot web application that lets users build, customize, and persist workout plans. Users choose a workout template (e.g. Full-Body, Upper-Body) and a difficulty plan (Beginner, Advanced, Custom). The system assembles a session made up of warmup, main, and cooldown phases using three exercise types — **Strength**, **Cardio**, and **Yoga**. Sessions can be saved to the workout history database and viewed on the History page.

The UI is a simple HTML front end served via Thymeleaf; persisted state lives in an H2 database accessed through a Spring Data JPA repository.

---

## How to Run

```bash
./gradlew bootRun
```

Then open `http://localhost:8080/`. The H2 console (for inspecting saved workouts) is at `http://localhost:8080/h2-console` (JDBC URL `jdbc:h2:mem:fitforge`, user `sa`, no password).

Run tests:

```bash
./gradlew test
```

---

## Design Patterns Used

### 1. Factory — `com.fitforge.factory.ExerciseFactory`

Centralizes creation of `Exercise` instances for any registered type (`Strength`, `Cardio`, `Yoga`). Crucially, the factory is backed by a `Map<String, Function<String, Exercise>>` registry rather than a switch statement — adding a new exercise type means calling `register(...)`, not editing the factory. Callers receive an `Exercise` reference and never downcast.

```java
Exercise e = factory.create("Strength");   // returns Strength via abstraction
```

### 2. Strategy — `com.fitforge.strategy.WorkoutPlanStrategy`

Three interchangeable difficulty strategies: `BeginnerPlan`, `AdvancedPlan`, `CustomPlan`. Each decides how many exercises go into each phase and how intensity is scaled. A `Workout` holds a single `WorkoutPlanStrategy` reference and delegates — it never branches on difficulty. Strategies are injected into `WorkoutService` as a `List<WorkoutPlanStrategy>` that Spring auto-populates with every bean implementing the interface.

```java
workout = builder.apply(factory, plan);   // plan is any WorkoutPlanStrategy
workout.build();                          // runs that plan polymorphically
```

### 3. Template Method — `com.fitforge.domain.Workout`

`Workout` is an abstract class whose `final build()` method defines the session flow:

```java
public final void build() {
    buildWarmup(warmup, plan.warmupExerciseCount());
    buildMain(main, plan.mainExerciseCount());
    buildCooldown(cooldown, plan.cooldownExerciseCount());
    plan.apply(allExercises());
}
```

Concrete subclasses (`FullBodyWorkout`, `UpperBodyWorkout`) implement each phase, but the overall algorithm is fixed. This is the classic "hold the structure constant while varying the steps" shape.

### 4. Repository — `com.fitforge.repository.WorkoutSessionRepository`

`WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long>` is a Spring Data JPA repository. We declare the interface only — Spring generates the implementation at startup. The service layer saves `WorkoutSession` aggregates and queries history through this abstraction with no knowledge of SQL or Hibernate.

```java
repository.save(session);
repository.findAllByOrderByCompletedAtDesc();
```

---

## OO Design Principles

| Principle | How it's applied |
|---|---|
| **Code to abstractions** | Service/controller code references `Exercise`, `Workout`, and `WorkoutPlanStrategy` — never `Strength`, `FullBodyWorkout`, or `BeginnerPlan` |
| **Polymorphism** | `Exercise.describe()` and `Exercise.scaleIntensity()` are overridden per subclass; `WorkoutPlanStrategy.apply()` is dispatched polymorphically; the factory registry eliminates the switch that would otherwise branch on type |
| **Dependency Injection** | Every collaborator is constructor-injected: `WorkoutService(ExerciseFactory, WorkoutSessionRepository, List<WorkoutPlanStrategy>)`, `Workout(ExerciseFactory, WorkoutPlanStrategy)`, `WorkoutController(WorkoutService)`. No `new` calls on collaborators anywhere in the graph |
| **No switch / if-else on type** | The factory uses a `Map` registry; `WorkoutService` uses a strategy map and template map; exercise subclasses respond to `scaleIntensity(factor)` without the caller asking what type they are |
| **Open/Closed** | New exercise types: `factory.register(...)`. New strategies: implement the interface + `@Component`. New workout templates: add an entry to `workoutBuilders`. No existing code changes required |

---

## Architecture Overview

```
src/main/java/com/fitforge/
├── FitForgeApplication.java         — Spring Boot entry point
├── domain/
│   ├── Exercise.java                — abstract base (Strength/Cardio/Yoga)
│   ├── Strength.java, Cardio.java, Yoga.java
│   ├── Workout.java                 — TEMPLATE METHOD (abstract)
│   ├── workouts/
│   │   ├── FullBodyWorkout.java     — concrete template subclass
│   │   └── UpperBodyWorkout.java    — concrete template subclass
│   ├── WorkoutSession.java          — @Entity (aggregate root for history)
│   └── ExerciseRecord.java          — @Embeddable snapshot
├── factory/
│   └── ExerciseFactory.java         — FACTORY (Map-based, no switch)
├── strategy/
│   ├── WorkoutPlanStrategy.java     — STRATEGY interface
│   ├── BeginnerPlan.java
│   ├── AdvancedPlan.java
│   └── CustomPlan.java
├── repository/
│   └── WorkoutSessionRepository.java — REPOSITORY (Spring Data JPA)
├── service/
│   └── WorkoutService.java          — orchestrates factory + strategy + repo
└── controller/
    └── WorkoutController.java       — HTTP + Thymeleaf
```

---

## Test Coverage

Tests live in `src/test/java/com/fitforge/` and comfortably exceed the 5-case requirement:

- `ExerciseTest` — 6 tests covering polymorphic scaling and set management
- `ExerciseFactoryTest` — 4 tests covering creation, naming, unknown types, and the extension point
- `StrategyTest` — 4 tests covering Beginner/Advanced/Custom behavior and polymorphic dispatch
- `WorkoutTemplateTest` — 3 tests covering the template-method flow, subclass divergence, and strategy application
- `WorkoutRepositoryTest` — 2 integration tests covering persistence and history ordering (boots a real Spring context with H2)
