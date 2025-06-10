package com.gym.dao;

import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountDao {
    void insert(Account account);

    Optional<Account> findByEmail(String email);

    boolean emailExists(String mail);

    List<Account> getAccountsByPage(int page, int size);

    int getCount();

    Optional<AccountWithRolesAndWallet> getAccountWithRolesAndWalletById(long id);

    List<AccountWithRolesAndWallet> getAccountsWithRolesAndWallet(int page, int size);

    void deleteById(long id);

    Optional<AccountWithRolesAndWallet> getAccountWithRolesAndWalletByEmail(String email);

    List<Account> getAccountsByRole(String role);
}
