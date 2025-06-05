package com.gym.dao;


import com.gym.model.Wallet;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Sql(statements = {
        "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
        "INSERT IGNORE INTO wallets (id, account_id, balance) VALUES (1, 1, 100.00)",
        "INSERT IGNORE INTO accounts (id, email, password) VALUES (2, 'test2@test.com', 'password123')",
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class WalletDaoTest extends AbstractDaoTest {
    private WalletDao walletDao;

    public WalletDaoTest(WalletDao walletDao) {
        this.walletDao = walletDao;
    }

    @Test
    void testGetWalletByAccountIdSuccess_ReturnWallet() {
        Optional<Wallet> optionalWallet = walletDao.getWalletByAccountId(1L);
        Wallet wallet = optionalWallet.get();
        assertNotNull(wallet);
        assertEquals(1L, wallet.getId());
        assertEquals(1L, wallet.getAccountId());
        assertEquals(0, new BigDecimal("100.00").compareTo(wallet.getBalance()));
    }

    @Test
    void testCreateWalletSuccess() {
        walletDao.createWallet(2L);
        Optional<Wallet> optionalWallet = walletDao.getWalletByAccountId(2L);
        assertTrue(optionalWallet.isPresent());
        Wallet wallet = optionalWallet.get();
        assertEquals(2L, wallet.getAccountId());
        assertEquals(0, new BigDecimal("0.00").compareTo(wallet.getBalance()));
    }
}
