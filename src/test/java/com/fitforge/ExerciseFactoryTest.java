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

class ExerciseFactoryTest {

    private final ExerciseFactory factory = new ExerciseFactory();

    @Test
    void create_returnsCorrectSubclass_forEveryKnownType() {
        assertInstanceOf(Strength.class, factory.create(Strength.TYPE));
        assertInstanceOf(Cardio.class,   factory.create(Cardio.TYPE));
        assertInstanceOf(Yoga.class,     factory.create(Yoga.TYPE));
    }

    @Test
    void create_withExplicitName_usesThatName() {
        Exercise e = factory.create(Strength.TYPE, "Deadlift");
        assertEquals("Deadlift", e.getName());
    }

    @Test
    void create_unknownType_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> factory.create("Swimming"));
    }

    @Test
    void drawUnique_returnsDistinctExercises_fromCatalog() {
        List<Exercise> drawn = factory.drawUnique(Strength.TYPE, 5);
        Set<String> names = drawn.stream()
                .map(Exercise::getName)
                .collect(Collectors.toSet());
        assertEquals(5, drawn.size());
        assertEquals(5, names.size());
    }

    @Test
    void register_extensionPoint_addsNewTypeWithoutEditingFactory() {
        class Pilates extends Exercise {
            Pilates(String name, int sets) { super(name, sets); }
            @Override public String getType() { return "Pilates"; }
            @Override public void scaleIntensity(double factor) { }
        }
        factory.register("Pilates",
                t -> new Pilates(t.name(), t.sets()),
                new ExerciseTemplate("Roll-up", 2, 10),
                new ExerciseTemplate("Hundred", 2, 100));

        Exercise p = factory.create("Pilates", "Roll-up");
        assertEquals("Pilates", p.getType());
        assertEquals("Roll-up", p.getName());
    }
}
