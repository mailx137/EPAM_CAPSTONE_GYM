package com.gym.service;

import com.gym.dao.CycleDao;
import com.gym.dto.response.Paginator;
import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

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
}
