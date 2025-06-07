package com.gym.controller;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@PreAuthorize("hasRole('CLIENT')")
@Controller
public class WalletController {
    private WalletService walletService;
    private MessageSource messageSource;

    public WalletController(WalletService walletService, MessageSource messageSource) {
        this.walletService = walletService;
        this.messageSource = messageSource;
    }

    @GetMapping("/wallet/top-up")
    public String showTopUpForm() {
        return "wallet/top-up/form";
    }

    @PostMapping("/wallet/top-up")
    public String topUp(int amount, HttpServletRequest httpServletRequest, @AuthenticationPrincipal AccountWithRolesAndWallet account) {
        walletService.topUp(amount, account.getId());
        String successMessage = messageSource.getMessage("alert.wallet.top_up.success", null, LocaleContextHolder.getLocale());
        return String.format("redirect:%s", httpServletRequest.getHeader("Referer"));
    }
}
