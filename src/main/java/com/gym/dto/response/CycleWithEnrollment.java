package com.gym.dto.response;

import com.gym.enums.AccountCycleEnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CycleWithEnrollment {
    private Long id;
    private String name;
    private String description;
    private int durationInDays;
    private boolean published;
    private BigDecimal price;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private long accountId;
    private Long trainerId;
    private AccountCycleEnrollmentStatus status;
    private LocalDateTime enrollmentCreatedAt;
    private LocalDateTime enrollmentUpdatedAt;
}
