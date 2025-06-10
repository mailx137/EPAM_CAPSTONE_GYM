package com.gym.controller.admin;


import com.gym.config.WebConfig;
import com.gym.controller.trainer.TrainerController;
import com.gym.dto.response.CycleForTrainerList;
import com.gym.dto.response.Paginator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class TrainerControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CycleService cycleService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new TrainerController(cycleService))
                .build();
    }

    @Test
    void testShowCyclesList() throws Exception {
        // Use anyInt() for page and size, and anyLong() for the trainer ID
        when(cycleService.getPaginatedCyclesByTrainerId(anyInt(), anyInt(), anyLong()))
                .thenReturn(new Paginator<>(0, 1, 5, List.of(new CycleForTrainerList())));
        mockMvc.perform(get("/trainer/cycles"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycles"))
                .andExpect(view().name("trainer/list"));
        verify(cycleService, times(1)).getPaginatedCyclesByTrainerId(anyInt(), anyInt(), anyLong());
    }
}
