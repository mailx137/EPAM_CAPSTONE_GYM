package com.gym.service;

import com.gym.dao.AccountDao;
import com.gym.dao.CycleDao;
import com.gym.dao.WalletDao;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.exception.NotEnoughBalanceException;
import com.gym.model.Cycle;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class WalletService {
    private final WalletDao walletDao;
    private final CycleDao cycleDao;
    private MessageSource messageSource;
    private AccountDao accountDao;

    public WalletService(WalletDao walletDao, CycleDao cycleDao, MessageSource messageSource, AccountDao accountDao) {
        this.walletDao = walletDao;
        this.cycleDao = cycleDao;
        this.messageSource = messageSource;
        this.accountDao = accountDao;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void topUp(int amount, long clientId) {
        walletDao.topUp(amount, clientId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void payCycle(long accountId, long cycleId) {
        AccountWithRolesAndWallet account = accountDao.getAccountWithRolesAndWalletById(accountId).orElseThrow(() ->
            new IllegalArgumentException(messageSource.getMessage("account.not.found", null, LocaleContextHolder.getLocale())
        ));

        Cycle cycle = cycleDao.findById(cycleId).orElseThrow(()->
            new IllegalArgumentException(messageSource.getMessage("cycle.not.found", null, LocaleContextHolder.getLocale())
        ));

        if (account.getWalletBalance().compareTo(cycle.getPrice()) < 0) {
            throw new NotEnoughBalanceException(messageSource.getMessage("alert.not_enough.balance", null, LocaleContextHolder.getLocale()));
        }

        walletDao.payCycle(accountId, cycleId);
        cycleDao.changeCycleStatus(cycleId, AccountCycleEnrollmentStatus.ACTIVE.name());
    }
}
