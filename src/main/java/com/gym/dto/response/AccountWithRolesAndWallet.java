package com.gym.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountWithRolesAndWallet {
    private long id;
    private String email;
    private boolean emailConfirmed;
    private boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Optional<Long> walletId;
    private Optional<BigDecimal> walletBalance;
    private List<String> roles;


}
