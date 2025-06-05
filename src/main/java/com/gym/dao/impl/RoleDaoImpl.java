package com.gym.dao.impl;

import com.gym.dao.RoleDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.enums.RoleType;
import com.gym.model.Role;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RoleDaoImpl implements RoleDao, JdbcCleanup {
    private DataSource dataSource;

    public RoleDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Role> getRolesByAccountId(long accountId) {
        String sql = "SELECT r.id, r.name FROM roles r " +
                     "JOIN accounts_roles ar ON r.id = ar.role_id " +
                     "WHERE ar.account_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);

            rs = stmt.executeQuery();
            List<Role> roles = new ArrayList<>();
            while (rs.next()) {
                Role role = new Role();
                role.setId(rs.getLong("id"));
                role.setName(RoleType.valueOf(rs.getString("name")));
                roles.add(role);
            }
            return roles;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void addRoleToAccount(long id, RoleType roleType) {

    }
}
