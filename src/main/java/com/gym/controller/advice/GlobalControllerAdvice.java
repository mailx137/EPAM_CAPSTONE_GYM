package com.gym.controller.advice;

import com.gym.dto.request.RegisterFormDto;
import com.gym.exception.AccountAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private Environment env;

    public GlobalControllerAdvice(Environment env) {
        this.env = env;
    }

    @ModelAttribute("activeProfile")
    public String getActiveProfile() {
        String[] profiles = env.getActiveProfiles();
        if (profiles.length > 0) {
            return profiles[0];
        }
        return "default";
    }

    @ExceptionHandler(AccountAlreadyExistsException.class)
    public String handleAccountAlreadyExists(AccountAlreadyExistsException ex, Model model) {
        RegisterFormDto emptyForm = new RegisterFormDto();
        model.addAttribute("registrationForm", emptyForm);
        model.addAttribute("errorMessage", ex.getMessage());

        return "register/form";
    }

    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }
}