package com.gym.service;


import com.gym.dao.AccountDao;
import com.gym.dao.RoleDao;
import com.gym.dao.WalletDao;
import com.gym.dto.request.RegisterFormDto;
import com.gym.enums.RoleType;
import com.gym.exception.AccountAlreadyExistsException;
import com.gym.model.Account;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
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

    @Mock
    private MessageSource messageSource;

    @ParameterizedTest
    @EnumSource(RoleType.class)
    void testRegisterAccountSuccess(RoleType roleType) {
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setEmail("test@email.com");
        registerFormDto.setPassword("password123");
        registerFormDto.setConfirmPassword("password123");

        // Set up the emailExists mock instead of findByEmail
        when(accountDao.emailExists(registerFormDto.getEmail()))
                .thenReturn(false);
        when(passwordEncoder.encode(registerFormDto.getPassword()))
                .thenReturn("qwerty123");

        doAnswer(invocation -> {
            Account acc = invocation.getArgument(0);
            acc.setId(1L);
            LocalDateTime now = LocalDateTime.now();
            acc.setCreatedAt(now);
            acc.setUpdatedAt(now);
            return null;
        }).when(accountDao).insert(any(Account.class));

        accountService.registerAccount(registerFormDto, roleType);

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

        verify(passwordEncoder).encode(registerFormDto.getPassword());
        verify(accountDao).emailExists(registerFormDto.getEmail());
        verify(accountDao, never()).findByEmail(registerFormDto.getEmail());
        verify(roleDao).addRoleToAccount(savedAccount.getId(), roleType);

        if (roleType == RoleType.CLIENT) {
            verify(walletDao).createWallet(savedAccount.getId());
        } else {
            verify(walletDao, never()).createWallet(anyLong());
        }
    }

    @Test
    void testRegisterAccountEmailAlreadyExists() {
        RegisterFormDto registerFormDto = new RegisterFormDto();
        registerFormDto.setEmail("test@test.com");

        when(accountDao.emailExists(registerFormDto.getEmail())).thenReturn(true);
        when(messageSource.getMessage(
                eq("validation.account.email.already_exists"),
                any(),
                any()
        )).thenReturn("Email already exists");

        assertThrows(AccountAlreadyExistsException.class, () -> accountService.registerAccount(registerFormDto, RoleType.CLIENT));
    }
}
