package com.fitforge;

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Yoga;
import com.fitforge.factory.ExerciseFactory;
import com.fitforge.factory.ExerciseFactory.ExerciseTemplate;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

// tests for design pattern #1: factory.
// each test nails down one specific property of the pattern — if you delete a test, a guarantee disappears.
class ExerciseFactoryTest {

    // plain `new` — no spring needed. unit tests stay fast and isolated. the factory is designed to work standalone.
    private final ExerciseFactory factory = new ExerciseFactory();

    // polymorphic return — asking for "Strength" hands back a Strength object. catches a corrupted builder map.
    // using Strength.TYPE (the constant) instead of the literal "Strength" so renaming the type is a single-point change.
    @Test
    void create_returnsCorrectSubclass_forEveryKnownType() {
        assertInstanceOf(Strength.class, factory.create(Strength.TYPE));
        assertInstanceOf(Cardio.class,   factory.create(Cardio.TYPE));
        assertInstanceOf(Yoga.class,     factory.create(Yoga.TYPE));
    }

    // explicit name path — "Deadlift" IS in the catalog so this hits the happy path.
    // (the fallback path — unknown name -> generic default template — still works too, it's just not tested here.)
    @Test
    void create_withExplicitName_usesThatName() {
        Exercise e = factory.create(Strength.TYPE, "Deadlift");
        assertEquals("Deadlift", e.getName());
    }

    // defensive test — unknown type fails LOUDLY with IllegalArgumentException, not silently with null.
    // the lambda () -> factory.create("Swimming") is how junit 5 captures "this code should throw".
    // throwing beats returning null because the error surfaces where the bug is, not ten frames away with a NPE.
    @Test
    void create_unknownType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("Swimming"));
    }

    // uniqueness guarantee on drawUnique — ask for 5, get 5 DISTINCT exercises.
    // the trick: dump the names into a Set. if any two exercises were duplicates, the set would be smaller than the list.
    @Test
    void drawUnique_returnsDistinctExercises_fromCatalog() {
        List<Exercise> drawn = factory.drawUnique(Strength.TYPE, 5);
        Set<String> names = drawn.stream()
                .map(Exercise::getName)
                .collect(Collectors.toSet());   // Set dedupes automatically.
        assertEquals(5, drawn.size());          // 5 exercises came back.
        assertEquals(5, names.size());          // ...and all 5 names are distinct.
    }

    // THE FLAGSHIP TEST — open/closed principle in action.
    // proves we can add a brand-new exercise type WITHOUT editing ExerciseFactory.java.
    // if the factory had a switch statement on type, this test would be impossible.
    // the map-of-builders design is what makes it work, and this test locks that design in place.
    @Test
    void register_extensionPoint_addsNewTypeWithoutEditingFactory() {
        // local class defined inside the test — a brand-new Exercise subclass.
        class Pilates extends Exercise {
            Pilates(String name, int sets) { super(name, sets); }
            @Override public String getType() { return "Pilates"; }
            @Override public void scaleIntensity(double factor) { } // no-op is fine for the test.
        }
        // register the new type with a builder lambda + a couple of templates. NOTHING in the factory source is changed.
        factory.register("Pilates",
                t -> new Pilates(t.name(), t.sets()),
                new ExerciseTemplate("Roll-up", 2, 10),
                new ExerciseTemplate("Hundred", 2, 100));

        // and now the factory can produce Pilates just like any built-in type.
        Exercise p = factory.create("Pilates", "Roll-up");
        assertEquals("Pilates", p.getType());
        assertEquals("Roll-up", p.getName());
    }
}
