package com.fitforge.repository; // design pattern #4: repository

import com.fitforge.domain.WorkoutSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

// the repository pattern in action — a seam between the domain and the database.
// nothing in this file has a body. spring data jpa generates the implementation at
// startup by reading the interface + method names, so we get save/find/delete for free
// AND custom queries for free, just from naming conventions.
@Repository // marks this as a data access component. technically redundant for JpaRepository subclasses (spring finds them anyway) but it makes the pattern intent explicit and opts into spring's exception translation (raw jdbc errors become DataAccessException).
public interface WorkoutSessionRepository extends JpaRepository<WorkoutSession, Long> {
    // extending JpaRepository<Entity, IdType> hands us a full CRUD toolbox:
    //   save(session)      — INSERT or UPDATE
    //   findById(id)       — returns Optional<WorkoutSession>
    //   findAll()          — every row
    //   deleteById(id)
    //   count()
    // plus pagination/sorting helpers. we never implement any of it.

    // query derivation: spring parses the method NAME and writes the SQL itself.
    // this one becomes: SELECT * FROM workout_session ORDER BY completed_at DESC.
    // used on the history page so newest workouts show up first.
    List<WorkoutSession> findAllByOrderByCompletedAtDesc();

    // same deal with a WHERE clause baked in from the name:
    // SELECT * FROM workout_session WHERE plan_id = ? ORDER BY completed_at DESC.
    // lets us filter history by which strategy built the workout ("beginner" / "advanced" / "custom").
    // if i typo a field name here (e.g. "planIdd"), spring fails at STARTUP — fast feedback.
    List<WorkoutSession> findByPlanIdOrderByCompletedAtDesc(String planId);
}
