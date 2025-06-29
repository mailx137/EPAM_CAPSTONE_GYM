package com.gym.service;

import com.gym.dao.AccountDao;
import com.gym.dao.RoleDao;
import com.gym.dao.WalletDao;
import com.gym.dto.request.RegisterFormDto;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.dto.response.Paginator;
import com.gym.dto.response.TrainerIdEmailDto;
import com.gym.enums.RoleType;
import com.gym.exception.AccountAlreadyExistsException;
import com.gym.model.Account;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AccountService {
    private AccountDao accountDao;
    private MessageSource messageSource;
    private PasswordEncoder passwordEncoder;
    private RoleDao roleDao;
    private WalletDao walletDao;

    public AccountService(AccountDao accountDao, MessageSource messageSource, PasswordEncoder passwordEncoder, RoleDao roleDao, WalletDao walletDao) {
        this.accountDao = accountDao;
        this.messageSource = messageSource;
        this.passwordEncoder = passwordEncoder;
        this.roleDao = roleDao;
        this.walletDao = walletDao;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void registerAccount(RegisterFormDto registerForm, RoleType roleType) {
        if (accountDao.emailExists(registerForm.getEmail())) {
            String msg = messageSource.getMessage(
                    "validation.account.email.already_exists",
                    new Object[]{registerForm.getEmail()},
                    LocaleContextHolder.getLocale()
            );
            throw new AccountAlreadyExistsException(msg);
        }

        Account newAccount = new Account();
        newAccount.setEmail(registerForm.getEmail());
        newAccount.setPassword(passwordEncoder.encode(registerForm.getPassword()));
        newAccount.setEmailConfirmed(false);
        newAccount.setBlocked(false);

        accountDao.insert(newAccount);

        roleDao.addRoleToAccount(newAccount.getId(), roleType);

        if (roleType.equals(RoleType.CLIENT)) {
            walletDao.createWallet(newAccount.getId());
        }
    }

    public Paginator<AccountWithRolesAndWallet> getPaginatedAdminAccountList(Integer page, Integer size) {
        if (page == null || size == null || page < 1 || size < 1) {
            throw new IllegalArgumentException("Page and size must be greater than 0");
        }
        return new Paginator<>(
                accountDao.getCount(),
                page,
                size,
                accountDao.getAccountsWithRolesAndWallet(page, size)
        );
    }

    @Transactional(rollbackFor = SQLException.class)
    public void deleteAccount(long id) {
        accountDao.deleteById(id);
    }

    public List<TrainerIdEmailDto> getAllTrainersIdAndEmail() {
        List<TrainerIdEmailDto> trainers = new ArrayList<>();
        List<Account> accounts = accountDao.getAccountsByRole(RoleType.TRAINER.name());
        for (Account account : accounts) {
            TrainerIdEmailDto trainer = new TrainerIdEmailDto();
            trainer.setId(account.getId());
            trainer.setEmail(account.getEmail());
            trainers.add(trainer);
        }
        return trainers;
    }
}
