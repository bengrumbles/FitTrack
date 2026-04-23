package com.fitforge.factory; // design pattern #1: factory

import com.fitforge.domain.Cardio;
import com.fitforge.domain.Exercise;
import com.fitforge.domain.Strength;
import com.fitforge.domain.Yoga;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

@Component // one instance is created at startup and injected anywhere a ExerciseFactory is asked for
public class ExerciseFactory {

// --------------------------------------------------------------------------
// Section 2: The ExerciseTemplate record
// --------------------------------------------------------------------------

    public record ExerciseTemplate( // single line auto generates a constructor, private final fields, public accessors
            String name, // exercise display name, e.g. "Bench Press"
            int sets, // number of sets, e.g. 3
            int value, // rep count for strength, minutes for cardio/yoga. e.g. 10
            String focus, // "upper", "lower", "core" for strength; "light", "power" for cardio; "mobility", "core" for yoga. used for filtering exercises by user focus area.
            String cues) {// coaching cues, e.g. "Retract shoulder blades; bar to mid-chest"

        public ExerciseTemplate(String name, int sets, int value) { // convenience constructor that takes only the first three fields and fills in defaults.
            this(name, sets, value, "full", "");
        }
    }

// --------------------------------------------------------------------------
// Section 3: two registeries
// --------------------------------------------------------------------------

    private final Map<String, Function<ExerciseTemplate, Exercise>> builders = new HashMap<>(); // maps a type name ("Strength") to a function that converts a template into a real exercise object.")
    private final Map<String, List<ExerciseTemplate>> catalog = new HashMap<>(); // maps a type name to a pool of pre-defined templates "Strength" -> [Bench Press, Pull-up, Back squat].

// --------------------------------------------------------------------------
// Section 4: constructor (populating the catalog)
// --------------------------------------------------------------------------

    public ExerciseFactory() {
        register(Strength.TYPE, // three calls to register(), one each for Strength, Cardio, and Yoga. 
                t -> {
                    Strength s = new Strength(t.name(), t.sets(), t.value());
                    s.setCues(t.cues());
                    return s; // a builder lambda that says "given a template, here's how to produce a concret Exercise"
                },
                new ExerciseTemplate("Bench Press",       3, 10, "upper", "Retract shoulder blades; bar to mid-chest"),
                new ExerciseTemplate("Overhead Press",    3, 8,  "upper", "Squeeze glutes; keep ribcage down"),
                new ExerciseTemplate("Pull-up",           3, 6,  "upper", "Drive elbows down, not back"),
                new ExerciseTemplate("Barbell Row",       3, 10, "upper", "Hinge to 45°; row to the belly"),
                new ExerciseTemplate("Bicep Curl",        3, 12, "upper", "Keep elbows pinned; no swing"),
                new ExerciseTemplate("Dumbbell Press",    3, 10, "upper", "Wrists stacked over elbows"),
                new ExerciseTemplate("Tricep Dip",        3, 10, "upper", "Lean slightly forward; elbows tight"),
                new ExerciseTemplate("Back Squat",        3, 8,  "lower", "Knees track over toes; brace hard"),
                new ExerciseTemplate("Deadlift",          3, 5,  "lower", "Bar over midfoot; neutral spine"),
                new ExerciseTemplate("Romanian Deadlift", 3, 8,  "lower", "Push hips back; soft knees"),
                new ExerciseTemplate("Walking Lunge",     3, 10, "lower", "Long step; trailing knee just off floor"),
                new ExerciseTemplate("Goblet Squat",      3, 10, "lower", "Elbows inside knees at the bottom"),
                new ExerciseTemplate("Hip Thrust",        3, 10, "lower", "Chin tucked; finish with full lockout"),
                new ExerciseTemplate("Calf Raise",        3, 15, "lower", "Pause at the top for a full second"),
                new ExerciseTemplate("Russian Twist",     3, 20, "core",  "Slow rotation from the ribs, not arms"),
                new ExerciseTemplate("Hanging Leg Raise", 3, 10, "core",  "No swinging; control the descent"),
                new ExerciseTemplate("Cable Crunch",      3, 15, "core",  "Round the spine; elbows to hips"));

        register(Cardio.TYPE,
                t -> {
                    Cardio c = new Cardio(t.name(), t.sets(), t.value());
                    c.setCues(t.cues());
                    return c;
                },
                new ExerciseTemplate("Rowing",            1, 20, "light", "Drive with legs first, then back, then arms"),
                new ExerciseTemplate("Treadmill Run",     1, 25, "light", "Easy conversational pace"),
                new ExerciseTemplate("Jump Rope",         3, 2,  "power", "Light on the toes; wrists, not arms"),
                new ExerciseTemplate("Stationary Bike",   1, 20, "light", "Steady cadence, 80–90 rpm"),
                new ExerciseTemplate("Stair Climber",     1, 15, "light", "Full steps; don't lean on the rails"),
                new ExerciseTemplate("Jumping Jacks",     3, 1,  "power", "Quick feet; full arm extension"),
                new ExerciseTemplate("Burpees",           3, 1,  "power", "Chest to floor; explosive jump up"),
                new ExerciseTemplate("Mountain Climbers", 3, 1,  "power", "Drive knees; flat back, hot feet"));

        register(Yoga.TYPE,
                t -> {
                    Yoga y = new Yoga(t.name(), t.sets(), t.value());
                    y.setCues(t.cues());
                    return y;
                },
                new ExerciseTemplate("Downward Dog",      2, 30, "mobility", "Press the floor away; heels reach down"),
                new ExerciseTemplate("Warrior II",        2, 45, "mobility", "Front knee over ankle; arms parallel"),
                new ExerciseTemplate("Plank",             3, 30, "core",     "Straight line from head to heels"),
                new ExerciseTemplate("Side Plank",        3, 30, "core",     "Stack shoulders; lift the top hip"),
                new ExerciseTemplate("Boat Pose",         3, 20, "core",     "Lift the chest; long spine, not round"),
                new ExerciseTemplate("Hollow Hold",       3, 20, "core",     "Low back pressed to floor; feet inches up"),
                new ExerciseTemplate("Child's Pose",      2, 60, "mobility", "Knees wide; forehead down; breathe"),
                new ExerciseTemplate("Cobra Pose",        2, 30, "mobility", "Elbows soft; chest forward, not up"),
                new ExerciseTemplate("Pigeon Pose",       2, 45, "mobility", "Square hips; relax into the stretch"),
                new ExerciseTemplate("Cat-Cow",           3, 20, "mobility", "Move with the breath; spine by spine"),
                new ExerciseTemplate("Mountain Pose",     2, 30, "mobility", "Grounded feet; long crown"));
    }

// --------------------------------------------------------------------------
// Section 5: public api
// --------------------------------------------------------------------------

    // the extension point. open/closed in action: add a brand-new exercise type (e.g. Pilates)
    // by calling register(...) once — no existing code changes required.
    // this is literally tested in ExerciseFactoryTest.register_extensionPoint_addsNewTypeWithoutEditingFactory.
    public final void register(String type,
                               Function<ExerciseTemplate, Exercise> builder,
                               ExerciseTemplate... templates) {
        builders.put(type, builder);                              // remember how to build this type.
        catalog.put(type, new ArrayList<>(List.of(templates)));   // and remember the pool of templates for it.
    }

    // create a random exercise of the given type (drawn from the catalog pool).
    public Exercise create(String type) {
        List<ExerciseTemplate> pool = catalog.getOrDefault(type, List.of()); // empty-list default avoids NPE on unknown type.
        if (pool.isEmpty()) {
            throw new IllegalArgumentException(
                    "No catalog for type: " + type + " (known: " + builders.keySet() + ")"); // clear error listing what IS known.
        }
        List<ExerciseTemplate> shuffled = new ArrayList<>(pool); // copy so we don't mutate the real catalog.
        Collections.shuffle(shuffled);
        return build(type, shuffled.get(0)); // grab index 0 of the shuffled copy = a random pick.
    }

    // create an exercise with an explicit name. if the name isn't in the catalog, use a generic default template.
    public Exercise create(String type, String name) {
        ExerciseTemplate template = findByName(type, name)
                .orElse(new ExerciseTemplate(name, 3, 10)); // Optional.orElse — fallback template if nothing matched.
        return build(type, template);
    }

    // draw `count` distinct exercises from the catalog of the given type. no focus filter = any focus allowed.
    public List<Exercise> drawUnique(String type, int count) {
        return drawUnique(type, null, count); // delegate to the focus-aware version with null = "any".
    }

    // draw `count` exercises of the given type, filtered by focus tag ("upper", "core", "mobility", ...).
    // this is the method every Workout subclass uses. if the filtered pool has fewer entries than requested,
    // the indexing wraps around rather than throwing.
    public List<Exercise> drawUnique(String type, String focus, int count) {
        List<ExerciseTemplate> pool = new ArrayList<>(
                catalog.getOrDefault(type, List.of())); // copy first so filtering doesn't touch the real catalog.
        if (focus != null) {
            pool.removeIf(t -> !focus.equals(t.focus())); // keep only templates whose focus matches — this is the tag system at work.
        }
        if (pool.isEmpty()) {
            throw new IllegalArgumentException(
                    "No catalog entries for type=" + type + " focus=" + focus); // catches typos like "uppr" early.
        }
        Collections.shuffle(pool); // randomize order so users don't see the same exercises every time.
        List<Exercise> result = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            result.add(build(type, pool.get(i % pool.size()))); // modulo wraps around if count > pool size.
        }
        return result;
    }

    // read-only view of all registered exercise types. used by tests and introspection.
    public Set<String> knownTypes() {
        return Collections.unmodifiableSet(builders.keySet()); // unmodifiable so callers can't corrupt the registry.
    }

    // read-only view of the template pool for a given type.
    public List<ExerciseTemplate> catalogFor(String type) {
        return List.copyOf(catalog.getOrDefault(type, List.of())); // immutable copy — same defensive pattern.
    }

    // private helper — the single place where the builder lambda actually runs. keeps create()/drawUnique() DRY.
    private Exercise build(String type, ExerciseTemplate template) {
        Function<ExerciseTemplate, Exercise> builder = builders.get(type);
        if (builder == null) {
            throw new IllegalArgumentException(
                    "Unknown exercise type: " + type + " (known: " + builders.keySet() + ")"); // same clear error shape as create().
        }
        return builder.apply(template); // runs the lambda registered by register() — e.g. "new Strength(name, sets, value)".
    }

    // private helper — look up a template by name (case-insensitive) and return Optional so callers handle "not found" explicitly.
    private Optional<ExerciseTemplate> findByName(String type, String name) {
        return catalog.getOrDefault(type, List.of()).stream()
                .filter(t -> t.name().equalsIgnoreCase(name)) // "deadlift" matches "Deadlift".
                .findFirst();
    }
}
