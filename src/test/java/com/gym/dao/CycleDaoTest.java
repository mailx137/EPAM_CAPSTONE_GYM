package com.gym.dao;

import com.gym.dto.response.CycleWithEnrollmentDto;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.model.AccountCycleEnrollment;
import com.gym.model.Cycle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.jdbc.Sql;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CycleDaoTest extends AbstractDaoTestConfig {

    private final CycleDao cycleDao;
    private final AccountCycleEnrollmentDao accountCycleEnrollmentDao;

    public CycleDaoTest(CycleDao cycleDao, AccountCycleEnrollmentDao accountCycleEnrollmentDao) {
        this.cycleDao = cycleDao;
        this.accountCycleEnrollmentDao = accountCycleEnrollmentDao;
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

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)"
    })
    @Test
    void testGetPublishedCycles() {
        List<Cycle> publishedCycles = cycleDao.getPublishedCycles();
        assertNotNull(publishedCycles);
        assertEquals(2, publishedCycles.size());
        assertEquals("Cycle 1", publishedCycles.get(0).getName());
        assertEquals("Cycle 3", publishedCycles.get(1).getName());
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
    })
    @Test
    void testEnrollCycle() {
        cycleDao.enrollCycle(1L, 1L);
        Optional<AccountCycleEnrollment> enrollment = accountCycleEnrollmentDao.getByAccountIdAndCycleId(1L, 1L);
        assertNotNull(enrollment);
        assertEquals(1L, enrollment.get().getAccountId(), "Account ID should match");
        assertEquals(1L, enrollment.get().getCycleId(), "Cycle ID should match");
        assertNull(enrollment.get().getTrainerId(), "Trainer ID should be null for this test");
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 1, 'ACTIVE')"
    })
    @Test
    void voidAccountCycleEnrollmentExists() {
        Optional<AccountCycleEnrollment> enrollment = accountCycleEnrollmentDao.getByAccountIdAndCycleId(1L, 1L);
        assertTrue(enrollment.isPresent(), "Enrollment should exist");
        assertEquals(1L, enrollment.get().getAccountId(), "Account ID should match");
        assertEquals(1L, enrollment.get().getCycleId(), "Cycle ID should match");
        assertEquals("ACTIVE", enrollment.get().getStatus().name(), "Status should be ACTIVE");
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'CLIENT')",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 1, 'PENDING')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 2, 'ACTIVE')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 3, 'PENDING')"
    })
    @Test
    void testGetAccountCyclePendingEnrollments() {
        List<AccountCycleEnrollment> pendingEnrollments = accountCycleEnrollmentDao.getAccountCyclePendingEnrollments(1L);
        assertNotNull(pendingEnrollments);
        assertEquals(2, pendingEnrollments.size(), "There should be 2 pending enrollments for account 1");
        assertTrue(pendingEnrollments.stream().anyMatch(e -> e.getCycleId() == 1L && e.getStatus().name().equals("PENDING")), "Cycle 1 should be pending");
        assertTrue(pendingEnrollments.stream().anyMatch(e -> e.getCycleId() == 3L && e.getStatus().name().equals("PENDING")), "Cycle 3 should be pending");
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'CLIENT')",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 1, 'PENDING')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 2, 'ACTIVE')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 3, 'PENDING')"
    })
    @Test
    void testGetAccountCyclePendingEnrollmentsCount() {
         int count = accountCycleEnrollmentDao.getAccountCyclePendingEnrollmentsCount(1L);
        assertEquals(2, count, "There should be 2 pending enrollments for account 1");
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (2, 'Cycle 2', 'Description 2', 60, false, 200.00)",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (3, 'Cycle 3', 'Description 3', 90, true, 300.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
            "INSERT IGNORE INTO roles (id, name) VALUES (1, 'CLIENT')",
            "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 1, 'PENDING')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 2, 'ACTIVE')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 3, 'PENDING')"
    })
    @Test
    void testGetCycleWithEnrollmentsWhereStatusIsPending() {
        List<CycleWithEnrollmentDto> cycles = cycleDao.getCyclesWithEnrollmentsByAccountIdAndStatus(1L, AccountCycleEnrollmentStatus.PENDING.name());
        assertNotNull(cycles);
        assertEquals(2, cycles.size(), "There should be 2 cycles with pending enrollments for account 1");
    }

    @Sql(statements = {
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Cycle 1', 'Description 1', 30, true, 100.00)",
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password')",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id, status) VALUES (1, 1, 'PENDING')"}
    )
    @Test
    void testChangeCycleStatus() {
        long cycleId = 1L;
        AccountCycleEnrollmentStatus newStatus = AccountCycleEnrollmentStatus.ACTIVE;

        cycleDao.changeCycleStatus(cycleId, newStatus.name());

        Optional<AccountCycleEnrollment> enrollment = accountCycleEnrollmentDao.getByAccountIdAndCycleId(1L, cycleId);
        assertTrue(enrollment.isPresent(), "Enrollment should exist after status change");
        assertEquals(newStatus, enrollment.get().getStatus(), "The status should be updated to ACTIVE");
    }
}
