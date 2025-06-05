package com.gym.controller;

import static org.hamcrest.Matchers.instanceOf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;

import com.gym.dto.request.RegistrationFormDto;

class RegistrationControllerTest extends AbstractWebMvcTest {
    @Test
    void testShowRegistrationForm() throws Exception {
        mockMvc.perform(get("/registration"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("registrationForm"))
                .andExpect(model().attribute("registrationForm", instanceOf(RegistrationFormDto.class)))
                .andExpect(view().name("registration/form"));
    }

}
