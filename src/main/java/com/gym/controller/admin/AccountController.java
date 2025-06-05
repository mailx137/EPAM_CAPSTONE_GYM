package com.gym.controller.admin;

import com.gym.service.AccountService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/accounts")
public class AccountController {
    private AccountService accountService;
    private MessageSource messageSource;

    public AccountController(AccountService accountService , MessageSource messageSource) {
        this.accountService = accountService;
        this.messageSource = messageSource;
    }

    @GetMapping
    public String showAccountsList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "5") Integer size,
                                   Model model) {
        model.addAttribute("accounts", accountService.getPaginatedAdminAccountList(page, size));
        return "admin/account/list";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String expectedMessage = messageSource.getMessage("alert.account.delete.success", null, LocaleContextHolder.getLocale());
        accountService.deleteAccount(id);
        redirectAttributes.addFlashAttribute("successMessage", expectedMessage);
        return "redirect:/admin/accounts";
    }
}
