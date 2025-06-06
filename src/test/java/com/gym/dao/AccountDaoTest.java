package com.gym.dao;


import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.enums.RoleType;
import com.gym.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class AccountDaoTest extends AbstractDaoTest {
    private AccountDao accountDao;

    public AccountDaoTest(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @Sql(statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
    })
    @Test
    void testFindByEmailSuccess_ReturnAccount() {
        Optional<Account> accountOptional = accountDao.findByEmail("test@test.com");
        assertTrue(accountOptional.isPresent());

        Account account = accountOptional.get();

        assertEquals("test@test.com", account.getEmail());
        assertFalse(account.isBlocked());
        assertFalse(account.isEmailConfirmed());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
        assertTrue(account.getCreatedAt().isBefore(LocalDateTime.now()));
        assertTrue(account.getUpdatedAt().isBefore(LocalDateTime.now()));
    }

    @Sql(statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
    })
    @Test
    void testEmailExistsSuccess_ReturnTrue() {
        assertTrue(accountDao.emailExists("test@test.com"));
    }

    @Test
    void testEmailExistsSuccess_ReturnFalse() {
        assertFalse(accountDao.emailExists("test@test.com"));
    }

    @Sql (statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (2, 'test2@test.com', 'password123')",
    })
    @Test
    void testGetCount() {
        int count = accountDao.getCount();
        assertEquals(2, count);
    }

    @Sql(statements = {
            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN')",
            "INSERT IGNORE INTO roles (id, name) VALUES (2, 'CLIENT')",
            "INSERT IGNORE INTO roles (id, name) VALUES (3, 'TRAINER')",

            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (2, 'test2@test.com', 'password123')",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (3, 'test3@test.com', 'password123')",

            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 2)",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (2, 2)",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (3, 3)",

            "INSERT IGNORE INTO wallets (id, account_id, balance) VALUES (1, 1, 100.0)",
    })
    @Test
    void testGetAccountsWithRolesAndWallet() {
        List<AccountWithRolesAndWallet> accounts = accountDao.getAccountsWithRolesAndWallet(1, 2);
        assertNotNull(accounts);
        assertEquals(2, accounts.size());
    }

    @Sql(statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",

            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'CLIENT')",

            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO wallets (id, account_id, balance) VALUES (1, 1, 100.0)"
    })
    @Test
    void testGetAccountWithRolesAndWalletById() {
        AccountWithRolesAndWallet account = accountDao.getAccountWithRolesAndWalletById(1).orElse(null);
        assertNotNull(account);
        assertEquals("test@test.com", account.getEmail());
        assertFalse(account.isBlocked());
        assertFalse(account.isEmailConfirmed());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
        assertTrue(account.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(account.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));

        // Проверка роли
        assertNotNull(account.getRoles());
        assertTrue(account.getRoles().contains("CLIENT"));

        // Проверка баланса кошелька через Optional
        assertTrue(account.getWalletBalance().isPresent());
        assertEquals(0, new BigDecimal("100.0").compareTo(account.getWalletBalance().get()));

        // Если хотите проверить id кошелька
        assertTrue(account.getWalletId().isPresent());
        assertEquals(1L, account.getWalletId().get());
    }

    @Sql(statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",

            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'CLIENT')",

            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO wallets (id, account_id, balance) VALUES (1, 1, 100.0)"
    })
    @Test
    void testGetAccountWithRolesAndWalletByEmail() {
        AccountWithRolesAndWallet account = accountDao.getAccountWithRolesAndWalletByEmail("test@test.com").orElse(null);
        assertNotNull(account);
        assertEquals("test@test.com", account.getEmail());
        assertFalse(account.isBlocked());
        assertFalse(account.isEmailConfirmed());
        assertNotNull(account.getCreatedAt());
        assertNotNull(account.getUpdatedAt());
        assertTrue(account.getCreatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(account.getUpdatedAt().isBefore(LocalDateTime.now().plusSeconds(1)));

        // Проверка роли
        assertNotNull(account.getRoles());
        assertTrue(account.getRoles().contains("CLIENT"));

        // Проверка баланса кошелька через Optional
        assertTrue(account.getWalletBalance().isPresent());
        assertEquals(0, new BigDecimal("100.0").compareTo(account.getWalletBalance().get()));

        // Если хотите проверить id кошелька
        assertTrue(account.getWalletId().isPresent());
        assertEquals(1L, account.getWalletId().get());
    }
}
