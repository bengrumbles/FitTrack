package com.fitforge;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Yoga;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// pins down the domain layer. every other test depends on these three subclasses behaving right —
// if this file fails, the rest of the test suite is meaningless.
// junit 5 (jupiter) — @Test + assert* imported statically. class and methods can be package-private.
class ExerciseTest {

    // the one mutator on the base Exercise class. documents that sets can grow at runtime (the UI's "+" button relies on it).
    @Test
    void increaseNumberOfSets_incrementsByOne() {
        Exercise e = new Strength("Pushups", 2, 10); // arrange
        e.increaseNumberOfSets();                    // act
        assertEquals(3, e.getNumberOfSets());        // assert — expected first, actual second. flipping them gives backwards error messages.
    }

    // guardrail test — catches a decrement that forgets Math.max(0, ...). negative sets would blow up template rendering.
    @Test
    void decreaseNumberOfSets_wontGoBelowZero() {
        Exercise e = new Strength("Pushups", 0, 10);
        e.decreaseNumberOfSets();
        assertEquals(0, e.getNumberOfSets()); // stays at zero, does NOT go to -1.
    }

    // polymorphism demo #1 — same method name, strength acts on reps.
    // this is what BeginnerPlan/AdvancedPlan rely on when they scale a workout up or down.
    @Test
    void scaleIntensity_isPolymorphic_strengthScalesReps() {
        Strength s = new Strength("Pushups", 3, 10);
        s.scaleIntensity(1.3);                // 10 * 1.3 = 13.
        assertEquals(13, s.getRepsPerSet());  // reps went up, not sets, not anything else.
    }

    // polymorphism demo #2 — cardio scales DURATION (minutes), not reps.
    @Test
    void scaleIntensity_isPolymorphic_cardioScalesDuration() {
        Cardio c = new Cardio("Rowing", 1, 20);
        c.scaleIntensity(0.5);                     // halve the minutes: 20 -> 10.
        assertEquals(10, c.getDurationMinutes());
    }

    // polymorphism demo #3 — yoga scales HOLD SECONDS.
    @Test
    void scaleIntensity_isPolymorphic_yogaScalesHold() {
        Yoga y = new Yoga("Plank", 2, 30);
        y.scaleIntensity(2.0);                   // 30s -> 60s.
        assertEquals(60, y.getHoldSeconds());
    }

    // describe() is overridden in each subclass and must contain the type label.
    // matters because ExerciseRecord.from(e) bakes describe() into a String at save time —
    // if the type discriminator disappeared, history rows would lose info.
    @Test
    void describe_includesTypeDiscriminator() {
        Exercise strength = new Strength("Squats", 3, 10);
        Exercise cardio   = new Cardio("Rowing", 1, 20);
        Exercise yoga     = new Yoga("Plank", 2, 30);

        assertTrue(strength.describe().contains("Strength")); // each describe() string identifies its own type.
        assertTrue(cardio.describe().contains("Cardio"));
        assertTrue(yoga.describe().contains("Yoga"));
    }
}
