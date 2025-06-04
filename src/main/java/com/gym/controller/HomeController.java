package com.gym.controller;

import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContextUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        return "home";
    }

    /**
     * Change the locale of the application.
     */
    @GetMapping("/set-locale")
    public String changeLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse,
            @RequestParam("lang") String lang) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(httpServletRequest);
        if (localeResolver != null) {
            localeResolver.setLocale(httpServletRequest, httpServletResponse, new Locale(lang));
        }
        return String.format("redirect:%s", httpServletRequest.getHeader("Referer"));
    }
}
