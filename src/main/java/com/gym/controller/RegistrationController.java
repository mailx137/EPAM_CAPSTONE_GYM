package com.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.gym.dto.request.RegisterFormDto;
import com.gym.service.AccountService;

import jakarta.validation.Valid;

@Controller
public class RegistrationController {
    private AccountService accountService;

    public RegistrationController(AccountService accountService) {
        this.accountService = accountService;
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
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register/form";
        }
        
        accountService.registerAccount(registerForm);
        return "redirect:/login";
    }
}
