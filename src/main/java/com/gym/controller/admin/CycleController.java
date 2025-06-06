package com.gym.controller.admin;

import com.gym.service.CycleService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CycleController {
    private CycleService cycleService;

    public CycleController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @GetMapping("/admin/cycles")
    public String showCyclesList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "5") Integer size,
                                 Model model) {
        model.addAttribute("cycles", cycleService.getPaginatedAllCycles(page, size));
        return "admin/cycle/list";
    }
}
