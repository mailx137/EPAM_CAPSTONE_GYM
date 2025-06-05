package com.gym.service;


import com.gym.dao.AccountDao;
import com.gym.dao.RoleDao;
import com.gym.dao.WalletDao;
import com.gym.dto.request.RegisterFormDto;
import com.gym.model.Account;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AccountServiceTest extends AbstractServiceTest {
    @InjectMocks
    private AccountService accountService;

    @Mock
    private AccountDao accountDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private WalletDao walletDao;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testRegisterAccountSuccess() {
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setEmail("test@email.com");
        registerFormDto.setPassword("password123");
        registerFormDto.setConfirmPassword("password123");

        when(accountDao.findByEmail(registerFormDto.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerFormDto.getPassword()))
                .thenReturn("qwerty123");

        doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(1L);
            return null;
        }).when(accountDao).insert(any(Account.class));

        accountService.registerAccount(registerFormDto);

        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        verify(accountDao).insert(accountCaptor.capture());
        Account savedAccount = accountCaptor.getValue();

        assertNotNull(savedAccount);
        assertEquals(1L, savedAccount.getId());
        assertEquals(registerFormDto.getEmail(), savedAccount.getEmail());
        assertEquals("qwerty123", savedAccount.getPassword());
        assertFalse(savedAccount.isEmailConfirmed());
        assertFalse(savedAccount.isBlocked());
        assertNotNull(savedAccount.getCreatedAt());
        assertNotNull(savedAccount.getUpdatedAt());
        assertTrue(savedAccount.getCreatedAt().isBefore(LocalDateTime.now()));
        assertTrue(savedAccount.getUpdatedAt().isBefore(LocalDateTime.now()));

        verify(accountDao, times(2)).findByEmail(registerFormDto.getEmail());
        verify(roleDao).assignRoleToAccount(1L);
        verify(walletDao).createWalletForAccount(1L);
    }
}
