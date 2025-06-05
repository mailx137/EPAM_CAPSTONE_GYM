package com.gym.controller.admin;

import com.gym.dto.response.AdminAccountListDto;
import com.gym.dto.response.Paginator;
import com.gym.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin/accounts")
public class AccountController {
    private AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public String showAccountsList(@RequestParam(required = false, defaultValue = "1") Integer page,
                                   @RequestParam(required = false, defaultValue = "5") Integer size,
                                   Model model) {
        model.addAttribute("accounts", accountService.getPaginatedAdminAccountList(page, size));
        return "admin/account/list";
    }
}
