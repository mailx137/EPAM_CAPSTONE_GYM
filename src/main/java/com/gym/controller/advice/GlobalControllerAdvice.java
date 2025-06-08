package com.gym.controller.advice;

import com.gym.dao.WalletDao;
import com.gym.dto.request.RegisterFormDto;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.exception.AccountAlreadyExistsException;
import com.gym.exception.AccountCycleEnrollmentAlreadyExistsException;
import com.gym.model.Account;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@ControllerAdvice
public class GlobalControllerAdvice {
    private WalletDao walletDao;
    private MessageSource messageSource;

    private Environment env;

    public GlobalControllerAdvice(Environment env, WalletDao walletDao, MessageSource messageSource) {
        this.env = env;
        this.walletDao = walletDao;
        this.messageSource = messageSource;
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

    @ExceptionHandler(AccountCycleEnrollmentAlreadyExistsException.class)
    public String handleAccountCycleEnrollmentAlreadyExists(AccountCycleEnrollmentAlreadyExistsException ex, Model model, RedirectAttributes redirectAttributes) {
        String msg = messageSource.getMessage("alert.account.cycle.enrollment.already.exists", null, ex.getMessage(), LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        return "redirect:/";
    }

    @ModelAttribute("currentUrl")
    public String currentUrl(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("walletBalance")
    public BigDecimal walletBalance(@AuthenticationPrincipal AccountWithRolesAndWallet account) {
        if (account == null || !account.getRoles().contains("CLIENT")) {
            return null;
        }
        return walletDao.getBalanceByAccountId(account.getId());

    }
}
