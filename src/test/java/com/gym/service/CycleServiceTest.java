package com.gym.service;

import com.gym.dao.AccountCycleEnrollmentDao;
import com.gym.dao.CycleDao;
import com.gym.dto.request.CycleFormDto;
import com.gym.dto.response.Paginator;
import com.gym.exception.AccountCycleEnrollmentAlreadyExistsException;
import com.gym.model.AccountCycleEnrollment;
import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

public class CycleServiceTest extends AbstractServiceTest{
    @InjectMocks
    private CycleService cycleService;

    @Mock
    private CycleDao cycleDao;

    @Mock
    private AccountCycleEnrollmentDao accountCycleEnrollmentDao;

    @Test
    void testGetCyclesByPage() {
        List<Cycle> cycles = List.of(new Cycle(), new Cycle(), new Cycle());
        when(cycleDao.getCyclesByPage(1, 10)).thenReturn(cycles);
        Paginator<Cycle> selectedCycles = cycleService.getPaginatedAllCycles(1, 10);
        assertNotNull(selectedCycles.getItems());
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

    @Test
    void testUpdateCycle() {
        CycleFormDto cycleFormDto = new CycleFormDto(
                "Updated Cycle",
                "Updated description",
                45,
                false,
                BigDecimal.valueOf(149.99)
        );

        Cycle existingCycle = new Cycle();
        existingCycle.setId(1L);
        existingCycle.setName("Original Cycle");
        existingCycle.setDescription("Original description");
        existingCycle.setDurationInDays(30);
        existingCycle.setPublished(true);
        existingCycle.setPrice(BigDecimal.valueOf(99.99));

        when(cycleDao.findById(1L)).thenReturn(java.util.Optional.of(existingCycle));
        doNothing().when(cycleDao).update(notNull(Cycle.class));

        cycleService.updateCycle(1L, cycleFormDto);

        verify(cycleDao, times(1)).findById(1L);
        verify(cycleDao, times(1)).update(notNull(Cycle.class));
    }

    @Test
    void testEnrollCycle() {
        long accountId = 2L;
        long cycleId = 1L;
        when(accountCycleEnrollmentDao.existsByAccountIdAndCycleId(accountId, cycleId))
            .thenReturn(false)
            .thenReturn(true);

        doNothing().when(cycleDao).enrollCycle(accountId, cycleId);

        cycleService.enrollCycle(accountId, cycleId);

        verify(cycleDao, times(1)).enrollCycle(accountId, cycleId);

        assertThrows(AccountCycleEnrollmentAlreadyExistsException.class, () -> {
            cycleService.enrollCycle(accountId, cycleId);
        });
    }

}

