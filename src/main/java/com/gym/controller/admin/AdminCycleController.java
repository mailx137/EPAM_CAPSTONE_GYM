package com.gym.controller.admin;

import com.gym.dto.request.CycleFormDto;
import com.gym.model.Cycle;
import com.gym.service.CycleService;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@PreAuthorize("hasRole('ADMIN')")
@Controller
public class AdminCycleController {
    private CycleService cycleService;
    private MessageSource messageSource;

    public AdminCycleController(CycleService cycleService, MessageSource messageSource) {
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

    @GetMapping("/admin/cycle/add")
    public String showAddCycleForm(Model model) {
        model.addAttribute("cycleForm", new CycleFormDto());
        return "admin/cycle/form";
    }

    @PostMapping("/admin/cycle/add")
    public String createCycle(@Valid @ModelAttribute("cycleForm") CycleFormDto cycleFormDto, BindingResult result,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/cycle/form";
        }

        cycleService.createCycle(cycleFormDto);
        String successMessage = messageSource.getMessage("alert.cycle.create.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/cycles";
    }



    @GetMapping("/admin/cycle/edit/{id}")
    public String showEditCycleForm(@PathVariable Long id, Model model) {
        Cycle cycle = cycleService.findCycleById(id).orElseThrow(() -> new IllegalArgumentException("Invalid cycle ID: " + id));
        CycleFormDto cycleFormDto = cycleService.mapToFormDtoFromCycle(cycle);
        model.addAttribute("cycleForm", cycleFormDto);
        return "admin/cycle/form";
    }

    @PutMapping("/admin/cycle/edit/{id}")
    public String updateCycle(@PathVariable Long id, @Valid @ModelAttribute("cycleForm") CycleFormDto cycleFormDto,
                              BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/cycle/form";
        }

        cycleService.updateCycle(id, cycleFormDto);
        String successMessage = messageSource.getMessage("alert.cycle.update.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/cycles";
    }

    @GetMapping("/admin/cycle/active-list")
    public String showActiveCyclesList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                       @RequestParam(required = false, defaultValue = "5") Integer size,
                                       Model model) {
        model.addAttribute("cycles", cycleService.getPaginatedActiveCyclesWithTrainer(page, size));
        return "admin/cycle/active-list";
    }
}
