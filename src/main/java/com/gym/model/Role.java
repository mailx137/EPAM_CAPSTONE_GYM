package com.gym.model;

import com.gym.enums.RoleType;
import lombok.Data;

@Data
public class Role {
    private long id;
    private RoleType roleType;
}
