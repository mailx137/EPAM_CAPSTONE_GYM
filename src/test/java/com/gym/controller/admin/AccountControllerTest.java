package com.gym.controller.admin;

import com.gym.config.WebConfig;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.dto.response.Paginator;
import com.gym.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class AccountControllerTest {
    private MockMvc mockMvc;
    private AccountController accountController;

    @Mock
    private MessageSource messageSource;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        accountController = new AccountController(accountService, messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testShowAccountList() throws Exception {
        Paginator<AccountWithRolesAndWallet> accountsPaginator = new Paginator<>(0, 1, 10, List.of(new AccountWithRolesAndWallet()));
        when(accountService.getPaginatedAdminAccountList(1, 5)).thenReturn(accountsPaginator);

        mockMvc.perform(get("/admin/accounts"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("accounts",
                        allOf(
                                instanceOf(Paginator.class),
                                hasProperty("items", everyItem(instanceOf(AccountWithRolesAndWallet.class)))
                        )))
                .andExpect(view().name("admin/account/list"));
    }

    @Test
    void testDeleteAccount() throws Exception {
        String expectedMessage = messageSource.getMessage("alert.account.delete.success", null, LocaleContextHolder.getLocale());
        mockMvc.perform(get("/admin/account/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/accounts"))
                .andExpect(flash().attribute("successMessage", expectedMessage));
        verify(accountService, times(1)).deleteAccount(1L);

    }
}
