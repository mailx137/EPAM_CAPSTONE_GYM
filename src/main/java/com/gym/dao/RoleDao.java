package com.gym.dao;

import com.gym.model.Role;

import java.util.List;

public interface RoleDao {
    List<Role> getRolesByAccountId(long accountId);

    void assignRoleToAccount(Long any);
}
