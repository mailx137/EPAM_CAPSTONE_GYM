package com.gym.service;

import com.gym.dao.RoleDao;
import com.gym.dao.WalletDao;
import com.gym.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

@Service
public class RoleService {
    private RoleDao roleDao;
    private WalletDao walletDao;

public RoleService(RoleDao roleDao, WalletDao walletDao) {
        this.roleDao = roleDao;
        this.walletDao = walletDao;
    }

    public List<Role> getRolesByAccountId(long accountId) {
        return roleDao.getRolesByAccountId(accountId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void updateRolesByAccountId(long id, List<String> roles) {
        List<Long> roleIds = roleDao.getAllRoles().stream()
                .filter(role -> roles.contains(role.getName().name()))
                .map(Role::getId)
                .toList();
        roleDao.updateRolesByAccountId(id, roleIds);
        if (roles.contains("CLIENT")) {
            walletDao.createWallet(id);
        }
    }
}
