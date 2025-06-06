package com.gym.controller.admin;


import com.gym.config.WebConfig;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.dto.response.Paginator;
import com.gym.model.Cycle;
import com.gym.service.CycleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.instanceOf;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class CycleControllerTest {
    private MockMvc mockMvc;
    private CycleController cycleController;

    @Mock
    private CycleService cycleService;


    @BeforeEach
    void setUp() {
        cycleController = new CycleController(cycleService);
        mockMvc = MockMvcBuilders.standaloneSetup(cycleController).build();
    }

    @Test
    void testShowCyclesList() throws Exception {
        Paginator<Cycle> cyclePaginator = new Paginator<>(0, 1, 5, List.of(new Cycle()));
        when(cycleService.getPaginatedAllCycles(1, 5)).thenReturn(cyclePaginator);

        mockMvc.perform(get("/admin/cycles"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycles"))
                .andExpect(model().attribute("cycles",
                        allOf(
                                instanceOf(Paginator.class),
                                hasProperty("items", everyItem(instanceOf(Cycle.class)))
                        )))
                .andExpect(view().name("admin/cycle/list"));
    }

}
