package com.gym.dao;

import com.gym.model.AccountCycleEnrollment;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AccountCycleEnrollmentDaoTest extends AbstractDaoTestConfig {

    private AccountCycleEnrollmentDao accountCycleEnrollmentDao;

    public AccountCycleEnrollmentDaoTest(AccountCycleEnrollmentDao accountCycleEnrollmentDao) {
        this.accountCycleEnrollmentDao = accountCycleEnrollmentDao;
    }

    @Sql(statements = {
            "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
            "INSERT IGNORE INTO cycles (id, name, description, duration_in_days, published, price) VALUES (1, 'Test Cycle', 'Description of test cycle', 30, true, 99.99)",
            "INSERT IGNORE INTO account_cycle_enrollments (account_id, cycle_id) VALUES (1, 1)"
    })
    @Test
    public void testGetByAccountIdAndCycleId() {
        Optional<AccountCycleEnrollment> enrollment = accountCycleEnrollmentDao.getByAccountIdAndCycleId(1L, 1L);
        assertTrue(enrollment.isPresent(), "Enrollment should be present for account ID 1 and cycle ID 1");

        AccountCycleEnrollment accountCycleEnrollment = enrollment.get();
        assertEquals(1L, accountCycleEnrollment.getAccountId(), "Account ID should match");
        assertEquals(1L, accountCycleEnrollment.getCycleId(), "Cycle ID should match");
    }
}
