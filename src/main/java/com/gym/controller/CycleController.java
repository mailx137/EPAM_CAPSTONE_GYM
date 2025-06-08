package com.gym.controller;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.model.Account;
import com.gym.service.CycleService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CycleController {
    private MessageSource messageSource;
    private CycleService cycleService;

    public CycleController(MessageSource messageSource, CycleService cycleService) {
        this.messageSource = messageSource;
        this.cycleService = cycleService;
    }

    @PostMapping("/cycle/enroll/{id}")
    public String enrollInCycle(@PathVariable("id") Long cycleId, RedirectAttributes redirectAttributes, @AuthenticationPrincipal AccountWithRolesAndWallet account) {
        cycleService.enrollCycle(cycleId, account.getId());
        redirectAttributes.addFlashAttribute("successMessage", messageSource.getMessage("alert.cycle.enroll.success", null, LocaleContextHolder.getLocale()));
        return "redirect:/my-cycles";
    }
}
