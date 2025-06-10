package com.gym.dto.response;

import com.gym.enums.AccountCycleEnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CycleForTrainerList {
    private long enrollmentId;
    private long cycleId;
    private long clientId;
    private long trainerId;
    private String clientEmail;
    private String cycleName;
    private String cycleDescription;
    private int cycleDurationInDays;
}
