package com.gym.service;

import com.gym.dao.RoleDao;
import com.gym.enums.RoleType;
import com.gym.model.Role;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.List;

import static org.mockito.Mockito.*;

public class RoleServiceTest extends AbstractServiceTest {
    @InjectMocks
    private RoleService roleService;

    @Mock
    private RoleDao roleDao;

    @Test
    void testUpdateRolesByAccountId() {
        long accountId = 1L;
        List<String> roleNames = List.of("CLIENT", "TRAINER");
        List<Long> roleIds = List.of(2L, 3L);

        Role clientRole = new Role();
        clientRole.setId(2L);
        clientRole.setName(RoleType.CLIENT);

        Role trainerRole = new Role();
        trainerRole.setId(3L);
        trainerRole.setName(RoleType.TRAINER);

        when(roleDao.getAllRoles()).thenReturn(List.of(clientRole, trainerRole));
        doNothing().when(roleDao).updateRolesByAccountId(accountId, roleIds);
        roleService.updateRolesByAccountId(accountId, roleNames);
        verify(roleDao).updateRolesByAccountId(accountId, roleIds);
    }
}
