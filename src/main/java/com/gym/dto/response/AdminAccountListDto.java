package com.gym.dto.response;

import com.gym.model.Role;
import com.gym.model.Wallet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminAccountListDto {
    private long id;
    private String email;
    private boolean emailConfirmed;
    private boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Wallet wallet;
    private List<Role> roles;
}
