package com.gym.controller.admin;

import com.gym.service.CycleService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@PreAuthorize("hasRole('ADMIN')")
@Controller
public class CycleController {
    private CycleService cycleService;
    private MessageSource messageSource;

    public CycleController(CycleService cycleService, MessageSource messageSource) {
        this.cycleService = cycleService;
        this.messageSource = messageSource;
    }

    @GetMapping("/admin/cycles")
    public String showCyclesList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                 @RequestParam(required = false, defaultValue = "5") Integer size,
                                 Model model) {
        model.addAttribute("cycles", cycleService.getPaginatedAllCycles(page, size));
        return "admin/cycle/list";
    }

    @DeleteMapping("/admin/cycle/delete/{id}")
    public String deleteCycle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        cycleService.deleteCycle(id);
        String successMessage = messageSource.getMessage("alert.cycle.delete.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/cycles";
    }
}
