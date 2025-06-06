package com.gym.service;

import com.gym.dao.CycleDao;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.Paginator;
import com.gym.model.Account;
import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class CycleServiceTest extends AbstractServiceTest{
    @InjectMocks
    private CycleService cycleService;

    @Mock
    private CycleDao cycleDao;

    @Test
    void testGetCyclesByPage() {
        List<Cycle> cycles = List.of(new Cycle(), new Cycle(), new Cycle());
        when(cycleDao.getCyclesByPage(1, 10)).thenReturn(cycles);
        Paginator<Cycle> selectedCycles = cycleService.getPaginatedAllCycles(1, 10);
        assertNotNull(selectedCycles);
        assertEquals(selectedCycles.getItems().size(), cycles.size());

        verify(cycleDao, times(1)).getCyclesByPage(1, 10);
    }

    @Test
    void testDeleteCycle() {
        Cycle cycle = new Cycle();
        cycle.setId(1L);
        doNothing().when(cycleDao).deleteById(cycle.getId());
        cycleService.deleteCycle(cycle.getId());
        verify(cycleDao, times(1)).deleteById(cycle.getId());
    }

    @Test
    void testCreateCycle() {
        CycleFormDto cycleFormDto = new CycleFormDto(
                "Test Cycle",
                "Description of test cycle",
                30,
                true,
                BigDecimal.valueOf(99.99)
        );

        Cycle cycle = new Cycle();
        cycle.setName(cycleFormDto.getName());
        cycle.setDescription(cycleFormDto.getDescription());
        cycle.setDurationInDays(cycleFormDto.getDurationInDays());
        cycle.setPublished(cycleFormDto.isPublished());
        cycle.setPrice(cycleFormDto.getPrice());

        doNothing().when(cycleDao).insert(notNull(Cycle.class));
        cycleService.createCycle(cycleFormDto);
        verify(cycleDao, times(1)).insert(notNull(Cycle.class));
    }

}
