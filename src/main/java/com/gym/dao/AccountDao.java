package com.gym.dao;

import com.gym.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {
    void insert(Account account);

    Optional<Account> findByEmail(String email);

    boolean emailExists(String mail);

    List<Account> getAccountsByPage(int page, int size);

    int getCount();
}
