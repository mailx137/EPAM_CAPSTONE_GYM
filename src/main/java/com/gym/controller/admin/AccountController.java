package com.gym.controller.admin;

import com.gym.enums.RoleType;
import com.gym.model.Role;
import com.gym.service.AccountService;
import com.gym.service.RoleService;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@Controller
public class AccountController {
    private AccountService accountService;
    private MessageSource messageSource;
    private RoleService roleService;

    public AccountController(AccountService accountService , MessageSource messageSource, RoleService roleService) {
        this.accountService = accountService;
        this.messageSource = messageSource;
        this.roleService = roleService;
    }

    @GetMapping("/admin/accounts")
    public String showAccountsList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "5") Integer size,
                                   Model model) {
        model.addAttribute("accounts", accountService.getPaginatedAdminAccountList(page, size));
        return "admin/account/list";
    }

    @GetMapping("/admin/account/delete/{id}")
    public String deleteAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        String expectedMessage = messageSource.getMessage("alert.account.delete.success", null, LocaleContextHolder.getLocale());
        accountService.deleteAccount(id);
        redirectAttributes.addFlashAttribute("successMessage", expectedMessage);
        return "redirect:/admin/accounts";
    }

    @GetMapping("/admin/account/change_roles/{id}")
    public String changeRoles(@PathVariable Long id, Model model) {
        List<String> accountRoles = roleService.getRolesByAccountId(id).stream().map(role -> role.getName().name()).toList();
        List<String> availableRoles = Arrays.stream(RoleType.values()).map(RoleType::name).toList();
        model.addAttribute("accountId", id);
        model.addAttribute("accountRoles", accountRoles);
        model.addAttribute("availableRoles", availableRoles);
        return "admin/account/change_roles";
    }

    @PutMapping("/admin/account/change_roles/{id}")
    public String updateRoles(@PathVariable Long id, @RequestParam List<String> roles, RedirectAttributes redirectAttributes) {
        roleService.updateRolesByAccountId(id, roles);
        String successMessage = messageSource.getMessage("alert.account.roles.update.success", null, LocaleContextHolder.getLocale());
        redirectAttributes.addFlashAttribute("successMessage", successMessage);
        return "redirect:/admin/accounts";
    }
}
