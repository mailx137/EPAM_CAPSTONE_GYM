package com.gym.controller;


import com.gym.config.WebConfig;
import com.gym.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class WalletControllerTest {
    private MockMvc mockMvc;

    @Mock
    private WalletService walletService;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        WalletController balanceController = new WalletController(walletService, messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(balanceController).build();
    }

    @Test
    void testShowTopUpForm() throws Exception {
        mockMvc.perform(get("/wallet/top-up"))
                .andExpect(status().isOk())
                .andExpect(view().name("wallet/top-up/form"));
    }

    @Test
    void testTopUpSuccess() throws Exception {
        when(messageSource.getMessage(eq("alert.wallet.top_up.success"), any(), any()))
                .thenReturn("User balance topped up");
        String expectedMessage = messageSource.getMessage("alert.wallet.top_up.success", null, LocaleContextHolder.getLocale());

        mockMvc.perform(post("/wallet/top-up").param("amount", "100"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", expectedMessage))
                .andExpect(redirectedUrl("/"));

        verify(walletService).topUp(eq(100), anyLong());
    }
}
