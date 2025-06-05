package com.gym.dao;

import com.gym.model.Account;

import java.util.Optional;

public interface AccountDao {
    void insert(Account account);

    Optional<Account> findByEmail(String email);
}
