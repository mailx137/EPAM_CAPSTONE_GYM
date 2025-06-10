package com.gym.controller.trainer;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.service.CycleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@PreAuthorize("hasRole('TRAINER')")
@Controller
public class TrainerController {
    private CycleService cycleService;

    public TrainerController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @GetMapping("/trainer/cycles")
    public String showCyclesList(
            @RequestParam(required = false, defaultValue = "1") Integer page,
            @RequestParam(required = false, defaultValue = "5") Integer size,
            Model model,
            @AuthenticationPrincipal AccountWithRolesAndWallet account
            ) {
        model.addAttribute("cycles", cycleService.getPaginatedCyclesByTrainerId(page, size, account.getId()));
        return "trainer/list";
    }
}
