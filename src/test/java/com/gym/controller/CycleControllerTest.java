package com.gym.controller;

import com.gym.config.WebConfig;
import com.gym.service.CycleService;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class CycleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private MessageSource messageSource;

    @Mock
    private CycleService cycleService;

    @BeforeEach
    void setUp() {
        CycleController cycleController = new CycleController(messageSource, cycleService);
        mockMvc = MockMvcBuilders.standaloneSetup(cycleController).build();
    }

    @Test
    void testEnrollCycle() throws Exception {
        String expectedMessage = messageSource.getMessage("alert.cycle.enroll.success", null, LocaleContextHolder.getLocale());
        when(messageSource.getMessage(eq("alert.cycle.enroll.success"), any(), any()))
                .thenReturn(expectedMessage);
        mockMvc.perform(post("/cycle/enroll/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("successMessage", expectedMessage))
                .andExpect(view().name("redirect:/my-cycles"));
        verify(cycleService).enrollCycle(anyLong(), eq(1L));
    }
}
