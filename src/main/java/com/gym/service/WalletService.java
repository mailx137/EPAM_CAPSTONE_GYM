package com.gym.service;

import com.gym.dao.WalletDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
public class WalletService {
    private WalletDao walletDao;

    public WalletService(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void topUp(int amount, long clientId) {
        walletDao.topUp(amount, clientId);
    }
}
