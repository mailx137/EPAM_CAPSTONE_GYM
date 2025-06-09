package com.gym.controller;

import com.gym.config.WebConfig;
import com.gym.dao.WalletDao;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.service.CycleService;
import com.gym.service.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class OrderControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CycleService cycleService;

    @Mock
    private WalletService walletService;

    @BeforeEach
    void setUp() {
        OrderController controller = new OrderController(cycleService, walletService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testShowOrders() throws Exception {
        mockMvc.perform(get("/orders")
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycles"))
                .andExpect(view().name("order/list"));

        verify(cycleService).getCyclesWithEnrollmentsByAccountIdAndStatus(anyLong(), eq(AccountCycleEnrollmentStatus.PENDING));
    }

    @Test
    void testPayCycle() throws Exception {
        Long cycleId = 1L;

        mockMvc.perform(post("/order/pay/" + cycleId)
                        .principal(() -> "user"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders"));

        verify(walletService).payCycle(anyLong(), eq(cycleId));
    }

    @Test
    void testAllCyclesByAccountId() throws Exception {
        Long accountId = 1L;

        mockMvc.perform(get("/my-cycles")
                        .principal(() -> "user"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycles"))
                .andExpect(view().name("cycle/list"));

        verify(cycleService).getCyclesWithEnrollmentsByAccountIdAndStatus(anyLong(), isNull());
    }
}
