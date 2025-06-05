package com.gym.controller;

import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.gym.dto.request.RegisterFormDto;
import com.gym.service.AccountService;

class RegistrationControllerTest extends AbstractWebMvcTest {
    private AccountService accountService = Mockito.mock(AccountService.class);

    @BeforeEach
    void setUp() {
        RegistrationController controller = new RegistrationController(accountService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
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

        verify(accountService).registerAccount(Mockito.any(RegisterFormDto.class));
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

        verify(accountService, never()).registerAccount(any());

    }
}
