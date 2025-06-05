package com.gym.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Account {
    private long id;
    private String email;
    private String password;
    private boolean emailConfirmed;
    private boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
