package com.gym.controller;

import com.gym.config.WebConfig;
import com.gym.dto.request.RegisterFormDto;
import com.gym.enums.RoleType;
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

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
class RegistrationControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @Mock
    private MessageSource messageSource;

    private RegistrationController registrationController;

    @BeforeEach
    void setUp() {
        registrationController = new RegistrationController(accountService, messageSource);
        mockMvc = MockMvcBuilders.standaloneSetup(registrationController).build();
    }

    @Test
    void testShowSignUpForm() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("registerForm"))
                .andExpect(model().attribute("registerForm", instanceOf(RegisterFormDto.class)))
                .andExpect(view().name("register/form"));
    }

    @Test
    void testRegisterSuccess() throws Exception {
        mockMvc.perform(post("/register")
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("confirmPassword", "password"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/login"));

        verify(accountService).registerAccount(any(RegisterFormDto.class), any(RoleType.class));
    }

    @Test
    void testRegisterValidationError() throws Exception {


        mockMvc.perform(post("/register")
                        .param("email", "")
                        .param("password", "short")
                        .param("confirmPassword", ""))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("registerForm", "email", "password", "confirmPassword"))
                .andExpect(view().name("register/form"));

        verify(accountService, never()).registerAccount(any(), any());
    }

}
