package com.fitforge;

import com.fitforge.domain.WorkoutSession;
import com.fitforge.repository.WorkoutSessionRepository;
import com.fitforge.service.WorkoutService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class WorkoutRepositoryTest {

    @Autowired WorkoutService service;
    @Autowired WorkoutSessionRepository repository;

    @Test
    void saveWorkout_persistsAndCanBeRetrieved() {
        long before = repository.count();

        WorkoutSession saved = service.saveWorkout("Full-Body", "Beginner");

        assertNotNull(saved.getId());
        assertEquals(before + 1, repository.count());

        WorkoutSession found = repository.findById(saved.getId()).orElseThrow();
        assertEquals("Full-Body Workout", found.getWorkoutName());
        assertEquals("Beginner", found.getPlanId());
        assertFalse(found.getExercises().isEmpty());
    }

    @Test
    void history_returnsMostRecentFirst() {
        service.saveWorkout("Full-Body",  "Beginner");
        service.saveWorkout("Upper-Body", "Advanced");

        List<WorkoutSession> history = service.history();
        assertTrue(history.size() >= 2);
        assertTrue(history.get(0).getCompletedAt()
                .compareTo(history.get(1).getCompletedAt()) >= 0);
    }
}
