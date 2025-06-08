package com.gym.dao;

import com.gym.model.AccountCycleEnrollment;

import java.util.Optional;

public interface AccountCycleEnrollmentDao {
    /**
     * Retrieves an AccountCycleEnrollment by account ID and cycle ID.
     *
     * @param accountId the ID of the account
     * @param cycleId   the ID of the cycle
     * @return an Optional containing the AccountCycleEnrollment if found, otherwise empty
     */
    Optional<AccountCycleEnrollment> getByAccountIdAndCycleId(Long accountId, Long cycleId);
}
