package com.gym.security;

import com.gym.dao.AccountDao;
import com.gym.dao.RoleDao;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.model.Account;
import com.gym.model.Role;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private PasswordEncoder passwordEncoder;
    private AccountDao accountDao;
    private RoleDao roleDao;

    public CustomAuthenticationProvider(PasswordEncoder passwordEncoder, AccountDao accountDao, RoleDao roleDao) {
        this.passwordEncoder = passwordEncoder;
        this.accountDao = accountDao;
        this.roleDao = roleDao;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        List<GrantedAuthority> authorities = new ArrayList<>();

        Account account = accountDao.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        if (!passwordEncoder.matches(password, account.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        AccountWithRolesAndWallet accountDto = accountDao.getAccountWithRolesAndWalletByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));

        List<Role> roles = roleDao.getRolesByAccountId(account.getId());
        roles.forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName().toString()));
        });

        return new UsernamePasswordAuthenticationToken(
                accountDto,
                null,
                authorities);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}