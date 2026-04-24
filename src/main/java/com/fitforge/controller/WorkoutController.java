package com.fitforge.controller;

import com.fitforge.domain.Workout;
import com.fitforge.domain.WorkoutSession;
import com.fitforge.service.WorkoutService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

// the HTTP layer — thin by design.
// controllers shouldn't think. they translate requests -> service calls -> view-model attributes.
// every real decision lives in WorkoutService; this class is mostly plumbing.
@Controller // returns view NAMES (thymeleaf templates), not JSON. use @RestController if we wanted a JSON API instead.
public class WorkoutController {

    private final WorkoutService service; // the one brain — controller just delegates to this.

    public WorkoutController(WorkoutService service) { // constructor-based DI again. spring wires in the single WorkoutService bean.
        this.service = service;
    }

    // home page — renders templates/index.html with the two dropdown lists populated.
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("templates", service.availableWorkoutTemplates()); // ["Full-Body", "Upper-Body", ...]
        model.addAttribute("plans", service.availablePlans());                // ["Beginner", "Advanced", "Custom"]
        return "index"; // tells spring to render src/main/resources/templates/index.html with the model above.
    }

    // preview = build a workout but DON'T save it. same page re-renders with the workout filled in.
    // POST (not GET) because each preview generates a random draw — we don't want it cached or bookmarked.
    @PostMapping("/preview")
    public String preview(@RequestParam String template,   // form field "template" -> this String.
                          @RequestParam String plan,       // form field "plan".
                          Model model) {
        Workout workout = service.buildWorkout(template, plan); // factory + strategy + template method all fire here.
        // re-populate dropdowns because index.html needs them again on re-render:
        model.addAttribute("templates", service.availableWorkoutTemplates());
        model.addAttribute("plans", service.availablePlans());
        model.addAttribute("selectedTemplate", template);      // so the form remembers what the user picked.
        model.addAttribute("selectedPlan", plan);
        model.addAttribute("workout", workout);                // the built workout, rendered by thymeleaf in the preview pane.
        return "index";
    }

    // save = build + persist + redirect. POST-Redirect-GET pattern — if we returned a view directly,
    // hitting refresh would re-submit the form and save twice. redirecting makes refresh safe.
    @PostMapping("/save")
    public String save(@RequestParam String template,
                       @RequestParam String plan,
                       Model model) {
        WorkoutSession saved = service.saveWorkout(template, plan); // builds, flattens, repository.save().
        model.addAttribute("savedId", saved.getId());               // id's available if we want to highlight the new row later.
        return "redirect:/history"; // 302 -> browser does a fresh GET on /history. refresh-safe.
    }

    // history page — list every saved session, newest first.
    @GetMapping("/history")
    public String history(Model model) {
        List<WorkoutSession> sessions = service.history(); // repository.findAllByOrderByCompletedAtDesc() under the hood.
        model.addAttribute("sessions", sessions);
        model.addAttribute("totalExercises",
                sessions.stream().mapToInt(s -> s.exerciseCount()).sum()); // tiny stat for the header — "47 exercises logged".
        return "history"; // templates/history.html.
    }

    // wipe the DB. POST (never GET!) because a GET that deletes data can be triggered by any crawler/prefetcher.
    @PostMapping("/history/clear")
    public String clearHistory() {
        service.clearHistory();
        return "redirect:/history"; // show the now-empty history page.
    }
}
