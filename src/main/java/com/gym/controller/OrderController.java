package com.gym.controller;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.service.CycleService;
import com.gym.service.WalletService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@PreAuthorize("hasRole('CLIENT')")
@Controller
public class OrderController {
    private final CycleService cycleService;
    private final WalletService walletService;
    private final MessageSource messageSource;

    public OrderController(CycleService cycleService, WalletService walletService, MessageSource messageSource) {
        this.cycleService = cycleService;
        this.walletService = walletService;
        this.messageSource = messageSource;
    }

    @GetMapping("/orders")
    public String showOrders(@AuthenticationPrincipal AccountWithRolesAndWallet account, Model model) {
        model.addAttribute("cycles", cycleService.getCyclesWithEnrollmentsByAccountIdAndStatus(
                account.getId(), AccountCycleEnrollmentStatus.PENDING));
        return "order/list";
    }

    @PostMapping("/order/pay/{cycle_id}")
    public String payCycle(@AuthenticationPrincipal AccountWithRolesAndWallet account, @PathVariable("cycle_id") Long cycle_id, RedirectAttributes redirectAttributes) {
        walletService.payCycle(account.getId(), cycle_id);
        redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("alert.order.payment.success", null, LocaleContextHolder.getLocale()));
        return "redirect:/orders";
    }

    @GetMapping("/my-cycles")
    public String showMyCycles(@AuthenticationPrincipal AccountWithRolesAndWallet account, Model model) {
        model.addAttribute("cycles", cycleService.getCyclesWithEnrollmentsByAccountIdAndStatus(
                account.getId(), null));
        return "cycle/list";
    }
}
