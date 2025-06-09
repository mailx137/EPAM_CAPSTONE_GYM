package com.gym.dao;

import com.gym.model.AccountCycleEnrollment;

import java.util.List;
import java.util.Optional;

public interface AccountCycleEnrollmentDao {
    Optional<AccountCycleEnrollment> getByAccountIdAndCycleId(Long accountId, Long cycleId);
    boolean existsByAccountIdAndCycleId(Long accountId, Long cycleId);
    List<AccountCycleEnrollment> getAccountCyclePendingEnrollments(Long accountId);
}
