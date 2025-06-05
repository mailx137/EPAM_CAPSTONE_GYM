package com.gym.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gym.dto.request.RegistrationFormDto;

@Controller
public class RegistrationController {
    /**
     * Displays the registration form.
     *
     * @return the name of the view to render
     */
    @GetMapping("/registration")
    public String showRegistrationForm(Model model) {
        model.addAttribute("registrationForm", new RegistrationFormDto());
        return "registration/form";
    }
}
