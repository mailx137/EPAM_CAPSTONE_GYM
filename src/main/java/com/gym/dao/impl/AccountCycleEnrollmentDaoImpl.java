package com.gym.dao.impl;

import com.gym.dao.AccountCycleEnrollmentDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.model.AccountCycleEnrollment;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountCycleEnrollmentDaoImpl implements AccountCycleEnrollmentDao, JdbcCleanup {
    private DataSource dataSource;

    public AccountCycleEnrollmentDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<AccountCycleEnrollment> getByAccountIdAndCycleId(Long accountId, Long cycleId) {
        String sql = "SELECT * FROM account_cycle_enrollments WHERE account_id = ? AND cycle_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);
            stmt.setLong(2, cycleId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                AccountCycleEnrollment enrollment = new AccountCycleEnrollment();
                enrollment.setId(rs.getLong("id"));
                enrollment.setAccountId(rs.getLong("account_id"));
                enrollment.setCycleId(rs.getLong("cycle_id"));
                long trainerId = rs.getLong("trainer_id");
                if (rs.wasNull()) {
                    enrollment.setTrainerId(null);
                } else {
                    enrollment.setTrainerId(trainerId);
                }
                enrollment.setStatus(AccountCycleEnrollmentStatus.valueOf(rs.getString("status")));
                enrollment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                enrollment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return Optional.of(enrollment);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public boolean existsByAccountIdAndCycleId(Long accountId, Long cycleId) {
        String sql = "SELECT COUNT(*) FROM account_cycle_enrollments WHERE account_id = ? AND cycle_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);
            stmt.setLong(2, cycleId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if enrollment exists for account " + accountId + " and cycle " + cycleId, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public List<AccountCycleEnrollment> getAccountCyclePendingEnrollments(Long accountId) {
        String sql = "SELECT * FROM account_cycle_enrollments WHERE account_id = ? AND status = 'PENDING'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);
            rs = stmt.executeQuery();
            List<AccountCycleEnrollment> enrollments = new java.util.ArrayList<>();
            while (rs.next()) {
                AccountCycleEnrollment enrollment = new AccountCycleEnrollment();
                enrollment.setId(rs.getLong("id"));
                enrollment.setAccountId(rs.getLong("account_id"));
                enrollment.setCycleId(rs.getLong("cycle_id"));
                long trainerId = rs.getLong("trainer_id");
                if (rs.wasNull()) {
                    enrollment.setTrainerId(null);
                } else {
                    enrollment.setTrainerId(trainerId);
                }
                enrollment.setStatus(AccountCycleEnrollmentStatus.valueOf(rs.getString("status")));
                enrollment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                enrollment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                enrollments.add(enrollment);
            }
            return enrollments;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving pending enrollments for account " + accountId, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public int getAccountCyclePendingEnrollmentsCount(Long accountId) {
        String sql = "SELECT COUNT(*) FROM account_cycle_enrollments WHERE account_id = ? AND status = 'PENDING'";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);
            rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting pending enrollments for account " + accountId, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }
}
