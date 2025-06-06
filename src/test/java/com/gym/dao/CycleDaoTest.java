package com.gym.dao;

import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CycleDaoTest extends AbstractDaoTest {

    private CycleDao cycleDao;

    public CycleDaoTest(CycleDao cycleDao) {
        this.cycleDao = cycleDao;
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)"
    })
    @ParameterizedTest
    @CsvSource(
            {
                    "1, 10",
                    "2, 1"
            }
    )
    void testGetCyclesByPage(int page, int size) {
        List<Cycle> cycles = cycleDao.getCyclesByPage(page, size);
        assertNotNull(cycles);

        if (page == 1) {
            assertEquals(3, cycles.size());
        }
        if (page == 2) {
            assertEquals(1, cycles.size());
        }
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)"
    })
    @Test
    void testGetCount() {
        int count = cycleDao.getCount();
        assertEquals(3, count);
    }

    @Test
    void testInsertCycle() {
        Cycle cycle = new Cycle();
        cycle.setName("New Cycle");
        cycle.setDescription("New Description");
        cycle.setDurationInDays(45);
        cycle.setPublished(true);
        cycle.setPrice(BigDecimal.valueOf(150.00));

        cycleDao.insert(cycle);

        List<Cycle> cycles = cycleDao.getCyclesByPage(1, 10);
        assertNotNull(cycles);
        assertEquals(1, cycles.stream().filter(c -> c.getName().equals("New Cycle")).count());
    }

    @Sql(statements = "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)")
    @Test
    void testFindCycleById() {
        Cycle cycle = cycleDao.findById(1L).orElseThrow();
        assertNotNull(cycle);
        assertEquals("Cycle 1", cycle.getName());
        assertEquals("Description 1", cycle.getDescription());
        assertEquals(30, cycle.getDurationInDays());
        assertEquals(true, cycle.isPublished());
        assertEquals(0, new BigDecimal(100).compareTo(cycle.getPrice()));
    }

    @Sql(statements = "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)")
    @Test
    void testUpdateCycle() {
        Cycle cycle = cycleDao.findById(1L).orElseThrow();
        cycle.setName("Updated Cycle");
        cycle.setDescription("Updated Description");
        cycle.setDurationInDays(45);
        cycle.setPublished(false);
        cycle.setPrice(BigDecimal.valueOf(150.00));

        cycleDao.update(cycle);

        Cycle updatedCycle = cycleDao.findById(1L).orElseThrow();
        assertNotNull(updatedCycle);
        assertEquals("Updated Cycle", updatedCycle.getName());
        assertEquals("Updated Description", updatedCycle.getDescription());
        assertEquals(45, updatedCycle.getDurationInDays());
        assertEquals(false, updatedCycle.isPublished());
        assertEquals(0, new BigDecimal(150).compareTo(updatedCycle.getPrice()));
    }
}
