package com.gym.dao;

import com.gym.enums.RoleType;
import com.gym.model.Role;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Sql(statements = {
        "DELETE FROM accounts_roles",
        "DELETE FROM accounts",
        "DELETE FROM roles",
        "INSERT INTO roles (id, name) VALUES (1, 'ADMIN')",
        "INSERT INTO roles (id, name) VALUES (2, 'CLIENT')",
        "INSERT INTO roles (id, name) VALUES (3, 'TRAINER')",
        "INSERT INTO accounts (id, email, password) VALUES (1, 'test@test.com', 'password123')",
        "INSERT INTO accounts_roles (account_id, role_id) VALUES (1, 1)",
        "INSERT INTO accounts_roles (account_id, role_id) VALUES (1, 2)"
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

    @Test
    void testGetAllRolesSuccess_ReturnAllRoles() {
        List<Role> roles = roleDao.getAllRoles();
        assertEquals(3, roles.size());
    }

    @Test
    void testUpdateRolesByAccountIdSuccess() {
        List<Long> roleIds = List.of(2L, 3L);
        roleDao.updateRolesByAccountId(1, roleIds);

        List<Role> roles = roleDao.getRolesByAccountId(1);
        assertEquals(2, roles.size());

        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleType.CLIENT));
        assertTrue(roles.stream().anyMatch(role -> role.getName() == RoleType.TRAINER));
        assertFalse(roles.stream().anyMatch(role -> role.getName() == RoleType.ADMIN));
    }

}
