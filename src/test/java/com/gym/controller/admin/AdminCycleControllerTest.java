package com.gym.controller.admin;


import com.gym.config.WebConfig;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.ActiveCycleListDto;
import com.gym.dto.response.Paginator;
import com.gym.model.Cycle;
import com.gym.service.AccountService;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = {WebConfig.class})
public class AdminCycleControllerTest {
    private MockMvc mockMvc;

    @Mock
    private CycleService cycleService;

    @Mock
    private MessageSource messageSource;

    @Mock
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        AdminCycleController cycleController = new AdminCycleController(cycleService, messageSource, accountService);
        mockMvc = MockMvcBuilders
                .standaloneSetup(cycleController)
                .build();
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

    @Test
    void testDeleteCycle() throws Exception {
        String expectedMessage = messageSource.getMessage("alert.cycle.delete.success", null, LocaleContextHolder.getLocale());
        mockMvc.perform(delete("/admin/cycle/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cycles"))
                .andExpect(flash().attribute("successMessage", expectedMessage));
        verify(cycleService, times(1)).deleteCycle(1L);

    }

    @Test
    void testAddCycle() throws Exception {
        mockMvc.perform(get("/admin/cycle/add"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycleForm"))
                .andExpect(model().attribute("cycleForm", instanceOf(CycleFormDto.class)))
                .andExpect(view().name("admin/cycle/form"));
    }

    @Test
    void testCreateCycle() throws Exception {
        String expectedMessage = "Cycle created successfully!";

        when(messageSource.getMessage(eq("alert.cycle.create.success"), any(), any(Locale.class)))
                .thenReturn(expectedMessage);

        LocaleContextHolder.setLocale(Locale.ENGLISH);

        CycleFormDto cycleFormDto = new CycleFormDto(
                1L,
                "Test Cycle",
                "Description of test cycle",
                30,
                true,
                BigDecimal.valueOf(99.99)
        );

        mockMvc.perform(post("/admin/cycle/add")
                        .param("name", cycleFormDto.getName())
                        .param("description", cycleFormDto.getDescription())
                        .param("durationInDays", String.valueOf(cycleFormDto.getDurationInDays()))
                        .param("published", String.valueOf(cycleFormDto.isPublished()))
                        .param("price", cycleFormDto.getPrice().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cycles"))
                .andExpect(flash().attribute("successMessage", expectedMessage));

        verify(cycleService, times(1)).createCycle(any(CycleFormDto.class));

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testUpdateCycle() throws Exception {
        String expectedMessage = "Cycle updated successfully!";
        when(messageSource.getMessage(eq("alert.cycle.update.success"), any(), any(Locale.class)))
                .thenReturn(expectedMessage);

        LocaleContextHolder.setLocale(Locale.ENGLISH);

        CycleFormDto cycleFormDto = new CycleFormDto(
                "Updated Cycle",
                "Updated description",
                45,
                false,
                BigDecimal.valueOf(149.99)
        );

        mockMvc.perform(put("/admin/cycle/edit/1")
                        .param("name", cycleFormDto.getName())
                        .param("description", cycleFormDto.getDescription())
                        .param("durationInDays", String.valueOf(cycleFormDto.getDurationInDays()))
                        .param("published", String.valueOf(cycleFormDto.isPublished()))
                        .param("price", cycleFormDto.getPrice().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/cycles"))
                .andExpect(flash().attribute("successMessage", expectedMessage));

        verify(cycleService, times(1)).updateCycle(anyLong(), any(CycleFormDto.class));

        LocaleContextHolder.resetLocaleContext();
    }

    @Test
    void testEditCycle() throws Exception {
        Cycle cycle = new Cycle();
        cycle.setId(1L);
        cycle.setName("Test Cycle");
        cycle.setDescription("Description of test cycle");
        cycle.setDurationInDays(30);
        cycle.setPublished(true);
        cycle.setPrice(BigDecimal.valueOf(99.99));

        when(cycleService.findCycleById(1L)).thenReturn(Optional.of(cycle));

        when(cycleService.mapToFormDtoFromCycle(any(Cycle.class))).thenReturn(
                new CycleFormDto("Test Cycle", "Description", 30, true, BigDecimal.valueOf(99.99))
        );
        mockMvc.perform(get("/admin/cycle/edit/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("cycleForm"))
                .andExpect(model().attribute("cycleForm", instanceOf(CycleFormDto.class)))
                .andExpect(view().name("admin/cycle/form"));
        verify(cycleService, times(1)).findCycleById(1L);
    }

    @Test
    void testShowActiveCyclesList() throws Exception {
        when(cycleService.getPaginatedActiveCyclesWithTrainer(1, 5)).thenReturn(new Paginator<>(0, 1, 5, List.of(new ActiveCycleListDto())));
        mockMvc.perform(get("/admin/cycle/active-list")
                        .param("page", "1")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/cycle/active-list"))
                .andExpect(model().attributeExists("cycles"))
                .andExpect(model().attributeExists("trainerList"))
                .andExpect(model().attribute("trainerList", instanceOf(List.class)))
                .andExpect(model().attribute("cycles", instanceOf(Paginator.class)));
        verify(cycleService, times(1)).getPaginatedActiveCyclesWithTrainer(anyInt(), anyInt());
        verify(accountService, times(1)).getAllTrainersIdAndEmail();
    }
}
