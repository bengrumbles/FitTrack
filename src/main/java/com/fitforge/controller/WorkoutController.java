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

@Controller
public class WorkoutController {

    private final WorkoutService service;

    public WorkoutController(WorkoutService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("templates", service.availableWorkoutTemplates());
        model.addAttribute("plans", service.availablePlans());
        return "index";
    }

    @PostMapping("/preview")
    public String preview(@RequestParam String template,
                          @RequestParam String plan,
                          Model model) {
        Workout workout = service.buildWorkout(template, plan);
        model.addAttribute("templates", service.availableWorkoutTemplates());
        model.addAttribute("plans", service.availablePlans());
        model.addAttribute("selectedTemplate", template);
        model.addAttribute("selectedPlan", plan);
        model.addAttribute("workout", workout);
        return "index";
    }

    @PostMapping("/save")
    public String save(@RequestParam String template,
                       @RequestParam String plan,
                       Model model) {
        WorkoutSession saved = service.saveWorkout(template, plan);
        model.addAttribute("savedId", saved.getId());
        return "redirect:/history";
    }

    @GetMapping("/history")
    public String history(Model model) {
        List<WorkoutSession> sessions = service.history();
        model.addAttribute("sessions", sessions);
        model.addAttribute("totalExercises",
                sessions.stream().mapToInt(s -> s.exerciseCount()).sum());
        return "history";
    }

    @PostMapping("/history/clear")
    public String clearHistory() {
        service.clearHistory();
        return "redirect:/history";
    }
}
