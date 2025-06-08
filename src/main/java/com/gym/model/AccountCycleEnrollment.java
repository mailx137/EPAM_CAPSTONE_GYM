package com.gym.model;

import com.gym.enums.AccountCycleEnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCycleEnrollment {
    private long id;
    private long accountId;
    private long cycleId;
    private Long trainerId;
    private AccountCycleEnrollmentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
