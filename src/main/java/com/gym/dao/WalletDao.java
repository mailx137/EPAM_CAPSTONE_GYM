package com.gym.dao;

import com.gym.model.Wallet;

import java.math.BigDecimal;

public interface WalletDao {
    void createWallet(long id);

    Wallet getWalletByAccountId(long accountId);

    void topUp(int amount, long clientId);

    BigDecimal getBalanceByAccountId(long accountId);
}
