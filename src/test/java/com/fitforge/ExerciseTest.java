package com.fitforge;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Yoga;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExerciseTest {

    @Test
    void increaseNumberOfSets_incrementsByOne() {
        Exercise e = new Strength("Pushups", 2, 10);
        e.increaseNumberOfSets();
        assertEquals(3, e.getNumberOfSets());
    }

    @Test
    void decreaseNumberOfSets_wontGoBelowZero() {
        Exercise e = new Strength("Pushups", 0, 10);
        e.decreaseNumberOfSets();
        assertEquals(0, e.getNumberOfSets());
    }

    @Test
    void scaleIntensity_isPolymorphic_strengthScalesReps() {
        Strength s = new Strength("Pushups", 3, 10);
        s.scaleIntensity(1.3);
        assertEquals(13, s.getRepsPerSet());
    }

    @Test
    void scaleIntensity_isPolymorphic_cardioScalesDuration() {
        Cardio c = new Cardio("Rowing", 1, 20);
        c.scaleIntensity(0.5);
        assertEquals(10, c.getDurationMinutes());
    }

    @Test
    void scaleIntensity_isPolymorphic_yogaScalesHold() {
        Yoga y = new Yoga("Plank", 2, 30);
        y.scaleIntensity(2.0);
        assertEquals(60, y.getHoldSeconds());
    }

    @Test
    void describe_includesTypeDiscriminator() {
        Exercise strength = new Strength("Squats", 3, 10);
        Exercise cardio   = new Cardio("Rowing", 1, 20);
        Exercise yoga     = new Yoga("Plank", 2, 30);

        assertTrue(strength.describe().contains("Strength"));
        assertTrue(cardio.describe().contains("Cardio"));
        assertTrue(yoga.describe().contains("Yoga"));
    }
}
