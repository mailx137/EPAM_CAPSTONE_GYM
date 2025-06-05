package com.gym.dao;


import com.gym.model.Account;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
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
}
