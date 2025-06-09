package com.gym.controller;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.service.CycleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@PreAuthorize("hasRole('CLIENT')")
@Controller
public class OrderController {
    private final CycleService cycleService;

    public OrderController(CycleService cycleService) {
        this.cycleService = cycleService;
    }

    @GetMapping("/orders")
    public String showOrders(@AuthenticationPrincipal AccountWithRolesAndWallet account, Model model) {
//        model.addAllAttributes("cycles", cycleService.getCyclesWithEnrollmentsByAccountIdAndStatus(account.getId(), AccountCycleEnrollmentStatus.PENDING));
        return "orders/list";
    }
}
