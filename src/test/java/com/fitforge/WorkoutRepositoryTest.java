package com.fitforge;

import com.fitforge.domain.WorkoutSession;
import com.fitforge.repository.WorkoutSessionRepository;
import com.fitforge.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// tests for design pattern #4: repository.
// the only test class that boots a real spring context — because the repository interface has no class behind it
// until spring generates the proxy at runtime. you can't `new` a JpaRepository.
@SpringBootTest // boots the full app context: h2 starts, hibernate generates the schema, every bean gets wired.
                // a leaner alternative is @DataJpaTest (jpa layer only) but we use @SpringBootTest because we need WorkoutService too.
class WorkoutRepositoryTest {

    // field injection is fine in tests (we don't own the class's construction). production code uses constructor injection.
    @Autowired WorkoutService service;
    @Autowired WorkoutSessionRepository repository;

    // full round-trip — save a workout, verify the row exists, pull it back, check the fields survived.
    @Test
    void saveWorkout_persistsAndCanBeRetrieved() {
        long before = repository.count();                                      // baseline — defensive against row leakage from other tests.

        WorkoutSession saved = service.saveWorkout("Full-Body", "Beginner");   // runs the whole chain: factory -> template method -> repo.save().

        assertNotNull(saved.getId());                                          // id was null before save; hibernate fills it in via GenerationType.IDENTITY. proves we actually hit the db.
        assertEquals(before + 1, repository.count());                          // count went up by exactly one. using `before + 1` keeps us independent of test order.

        // round-trip: pull the row back by id and check the fields match.
        WorkoutSession found = repository.findById(saved.getId()).orElseThrow(); // Optional.orElseThrow unwraps or fails loudly.
        assertEquals("Full-Body Workout", found.getWorkoutName());
        assertEquals("Beginner", found.getPlanId());
        assertFalse(found.getExercises().isEmpty());                             // the @ElementCollection rows came back too.
    }

    // pins down the custom query method — findAllByOrderByCompletedAtDesc.
    // that method has NO body. spring parsed its name and wrote the SQL. this test proves the parse worked.
    @Test
    void history_returnsMostRecentFirst() {
        service.saveWorkout("Full-Body",  "Beginner");  // save two sessions back-to-back.
        service.saveWorkout("Upper-Body", "Advanced");

        List<WorkoutSession> history = service.history();
        assertTrue(history.size() >= 2);                // `>= 2` not `== 2` because other tests may have inserted rows already.
        // index 0's completedAt should be newer than (or equal to) index 1's.
        // compareTo >= 0 means newest first. if the query accidentally returned ASC, this flips negative and fails.
        assertTrue(history.get(0).getCompletedAt()
                .compareTo(history.get(1).getCompletedAt()) >= 0);
    }
}
