package com.gym.service;

import com.gym.dao.RoleDao;
import com.gym.model.Role;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleService {
    private RoleDao roleDao;

public RoleService(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    public List<Role> getRolesByAccountId(long accountId) {
        return roleDao.getRolesByAccountId(accountId);
    }

    public void updateRolesByAccountId(long id, List<String> roles) {
        List<Long> roleIds = roleDao.getAllRoles().stream()
                .filter(role -> roles.contains(role.getName().name()))
                .map(Role::getId)
                .toList();
        roleDao.updateRolesByAccountId(id, roleIds);
    }
}
