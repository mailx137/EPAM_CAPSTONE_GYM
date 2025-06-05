package com.gym.dao;

import com.gym.enums.RoleType;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Sql(statements = {
        "INSERT IGNORE INTO roles (id, name) VALUES (1, 'ADMIN')",
        "INSERT IGNORE INTO roles (id, name) VALUES (2, 'CLIENT')",
        "INSERT IGNORE INTO roles (id, name) VALUES (3, 'TRAINER')",
        "INSERT IGNORE INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
        "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
        "INSERT IGNORE INTO accounts_roles (account_id, role_id) VALUES (1, 2)"
}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class RoleDaoTest extends AbstractDaoTest {
    private RoleDao roleDao;

    public RoleDaoTest(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Test
    void testGetRolesByAccountIdSuccess_ReturnRoles() {
        var roles = roleDao.getRolesByAccountId(1);

        assertEquals(2, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleType.ADMIN));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleType.CLIENT));
    }

    @Test
    void testAddRoleToAccountSuccess() {
        roleDao.addRoleToAccount(1, RoleType.TRAINER);
        var roles = roleDao.getRolesByAccountId(1);

        assertEquals(3, roles.size());
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleType.TRAINER));
    }
}
