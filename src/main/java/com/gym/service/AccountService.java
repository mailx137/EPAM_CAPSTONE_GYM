package com.gym.service;

import com.gym.dao.AccountDao;
import com.gym.dto.request.RegisterFormDto;
import com.gym.exception.AccountAlreadyExistsException;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class AccountService {
    private AccountDao accountDao;
    private MessageSource messageSource;

    public AccountService(AccountDao accountDao, MessageSource messageSource) {
        this.accountDao = accountDao;
        this.messageSource = messageSource;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void registerAccount(RegisterFormDto registerForm) {
        if (accountDao.emailExists(registerForm.getEmail())) {
            String msg = messageSource.getMessage(
                    "validation.account.email.already_exists",
                    new Object[]{registerForm.getEmail()},
                    LocaleContextHolder.getLocale()
            );
            throw new AccountAlreadyExistsException(msg);
        }
    }
}
