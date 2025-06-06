package com.gym.dao;

import com.gym.enums.RoleType;
import com.gym.model.Role;

import java.util.List;

public interface RoleDao {
    List<Role> getRolesByAccountId(long accountId);

    void addRoleToAccount(long id, RoleType roleType);

    List<Role> getAllRoles();

    void updateRolesByAccountId(long accountId, List<Long> roleIds);
}
