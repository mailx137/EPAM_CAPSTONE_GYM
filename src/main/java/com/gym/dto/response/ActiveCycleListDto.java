package com.gym.dto.response;

import com.gym.enums.AccountCycleEnrollmentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActiveCycleListDto {
    private Long id;
    private String name;
    private String clientId;
    private String clientEmail;
    private int durationInDays;
    private BigDecimal price;
    private Long enrollmentId;
    private Long trainerId;
    private String trainerEmail;
    private AccountCycleEnrollmentStatus status;
}
