package com.gym.model;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Account {
    private long id;
    private String email;
    private String password;
    private boolean emailConfirmed;
    private boolean blocked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
