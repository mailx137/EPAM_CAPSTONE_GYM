package com.gym.controller;

import com.gym.enums.RoleType;
import jakarta.validation.Valid;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gym.dto.request.RegisterFormDto;
import com.gym.service.AccountService;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RegistrationController {
    private AccountService accountService;
    private MessageSource messageSource;

    public RegistrationController(AccountService accountService, MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    /**
     * Displays the registration form.
     *
     * @return the name of the view to render
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("registerForm", new RegisterFormDto());
        return "register/form";
    }

    /* Handles the registration form submission */
    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registerForm") RegisterFormDto registerForm,
                           BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("registerForm", registerForm);
            return "register/form";
        }

        accountService.registerAccount(registerForm, RoleType.CLIENT);

        if (messageSource != null) {
            String successMessage = messageSource.getMessage("alert.account.created.success", null, LocaleContextHolder.getLocale());
            redirectAttributes.addFlashAttribute("successMessage", successMessage);
        }

        return "redirect:/login";
    }
}
