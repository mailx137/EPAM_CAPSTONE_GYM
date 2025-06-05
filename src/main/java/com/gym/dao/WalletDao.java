package com.gym.dao;

import com.gym.model.Wallet;

import java.util.Optional;

public interface WalletDao {
    void createWallet(long id);
    Optional<Wallet> getWalletByAccountId(long accountId);
}
