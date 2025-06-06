package com.gym.dao;

import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.jdbc.Sql;

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
}
