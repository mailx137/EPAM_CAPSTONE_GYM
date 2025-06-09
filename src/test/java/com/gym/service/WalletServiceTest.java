package com.gym.service;

import com.gym.dao.AccountDao;
import com.gym.dao.CycleDao;
import com.gym.dao.WalletDao;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class WalletServiceTest extends AbstractServiceTest {
    @InjectMocks
    private WalletService walletService;

    @Mock
    private WalletDao walletDao;

    @Mock
    private CycleDao cycleDao;

    @Mock
    private AccountDao accountDao;

    @Mock
    private MessageSource messageSource;

    @Test
    void testPayCycle() {
        // Setup
        long accountId = 1L;
        long cycleId = 1L;

        // Mock account
        AccountWithRolesAndWallet mockAccount = mock(AccountWithRolesAndWallet.class);
        when(mockAccount.getWalletBalance()).thenReturn(new BigDecimal("100.00"));
        when(accountDao.getAccountWithRolesAndWalletById(accountId)).thenReturn(Optional.of(mockAccount));

        // Mock cycle
        Cycle mockCycle = mock(Cycle.class);
        when(mockCycle.getPrice()).thenReturn(new BigDecimal("50.00"));
        when(cycleDao.findById(cycleId)).thenReturn(Optional.of(mockCycle));

        // Execute
        walletService.payCycle(accountId, cycleId);

        // Verify
        verify(walletDao, times(1)).payCycle(accountId, cycleId);
    }
}
