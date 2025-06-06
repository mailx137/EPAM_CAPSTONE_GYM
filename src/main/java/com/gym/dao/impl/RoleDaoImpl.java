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
        String sql = "INSERT IGNORE INTO accounts_roles (account_id, role_id) " +
                     "VALUES (?, (SELECT id FROM roles WHERE name = ?))";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.setString(2, roleType.name());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error adding role to account: " + id + ", role: " + roleType, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public List<Role> getAllRoles() {
        String sql = "SELECT id, name FROM roles";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
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
            throw new RuntimeException("Error fetching all roles", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void updateRolesByAccountId(int accountId, List<Long> roleIds) {
        String deleteSql = "DELETE FROM accounts_roles WHERE account_id = ?";
        String insertSql = "INSERT INTO accounts_roles (account_id, role_id) VALUES (?, ?)";
        Connection conn = null;
        PreparedStatement deleteStmt = null;
        PreparedStatement insertStmt = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            conn.setAutoCommit(false);

            // Delete existing roles for the account
            deleteStmt = conn.prepareStatement(deleteSql);
            deleteStmt.setInt(1, accountId);
            deleteStmt.executeUpdate();

            // Insert new roles
            insertStmt = conn.prepareStatement(insertSql);
            for (Long roleId : roleIds) {
                insertStmt.setInt(1, accountId);
                insertStmt.setLong(2, roleId);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    throw new RuntimeException("Error rolling back transaction", rollbackEx);
                }
            }
            throw new RuntimeException("Error updating roles for account: " + accountId, e);
        } finally {
            cleanupResources(null, deleteStmt, conn, dataSource);
            cleanupResources(null, insertStmt, null, null);
        }
    }
}
