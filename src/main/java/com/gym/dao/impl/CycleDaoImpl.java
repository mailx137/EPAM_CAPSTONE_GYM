package com.gym.dao.impl;

import com.gym.dao.CycleDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.dto.response.ActiveCycleListDto;
import com.gym.dto.response.CycleWithEnrollmentDto;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.model.Cycle;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class CycleDaoImpl implements CycleDao, JdbcCleanup {
    private DataSource dataSource;

    public CycleDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Cycle> getCyclesByPage(int page, int size) {
        String sql = "SELECT * FROM cycles ORDER BY id LIMIT ? OFFSET ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            rs = stmt.executeQuery();

            List<Cycle> cycles = new ArrayList<>();

            while (rs.next()) {
                Cycle cycle = new Cycle();
                cycle.setId(rs.getLong("id"));
                cycle.setName(rs.getString("name"));
                cycle.setDescription(rs.getString("description"));
                cycle.setDurationInDays(rs.getInt("duration_in_days"));
                cycle.setPublished(rs.getBoolean("published"));
                cycle.setPrice(rs.getBigDecimal("price"));
                cycle.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cycle.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                cycles.add(cycle);
            }
            return cycles;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cycles", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }

    }

    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM cycles";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            } else {
                return 0;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cycle count", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM cycles WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Cycle with id " + id + " not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cycle with id " + id, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public void insert(Cycle cycle) {
        String sql = "INSERT INTO cycles (name, description, duration_in_days, published, price) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cycle.getName());
            stmt.setString(2, cycle.getDescription());
            stmt.setInt(3, cycle.getDurationInDays());
            stmt.setBoolean(4, cycle.isPublished());
            stmt.setBigDecimal(5, cycle.getPrice());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to insert cycle");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error inserting cycle", e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public Optional<Cycle> findById(long id) {
        String sql = "SELECT * FROM cycles WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Cycle cycle = new Cycle();
                cycle.setId(rs.getLong("id"));
                cycle.setName(rs.getString("name"));
                cycle.setDescription(rs.getString("description"));
                cycle.setDurationInDays(rs.getInt("duration_in_days"));
                cycle.setPublished(rs.getBoolean("published"));
                cycle.setPrice(rs.getBigDecimal("price"));
                cycle.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cycle.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return Optional.of(cycle);
            } else {
                return Optional.empty();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cycle with id " + id, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void update(Cycle cycle) {
        String sql = "UPDATE cycles SET name = ?, description = ?, duration_in_days = ?, published = ?, price = ? WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, cycle.getName());
            stmt.setString(2, cycle.getDescription());
            stmt.setInt(3, cycle.getDurationInDays());
            stmt.setBoolean(4, cycle.isPublished());
            stmt.setBigDecimal(5, cycle.getPrice());
            stmt.setLong(6, cycle.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Cycle with id " + cycle.getId() + " not found");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error updating cycle with id " + cycle.getId(), e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public List<Cycle> getPublishedCycles() {
        String sql = "SELECT * FROM cycles WHERE published = true";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            List<Cycle> cycles = new ArrayList<>();

            while (rs.next()) {
                Cycle cycle = new Cycle();
                cycle.setId(rs.getLong("id"));
                cycle.setName(rs.getString("name"));
                cycle.setDescription(rs.getString("description"));
                cycle.setDurationInDays(rs.getInt("duration_in_days"));
                cycle.setPublished(rs.getBoolean("published"));
                cycle.setPrice(rs.getBigDecimal("price"));
                cycle.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cycle.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                cycles.add(cycle);
            }
            return cycles;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching published cycles", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void enrollCycle(long accountId, long cycleId) {
        String sql = "INSERT INTO account_cycle_enrollments (account_id, cycle_id, status, created_at) VALUES (?, ?, ?, NOW())";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);
            stmt.setLong(2, cycleId);
            stmt.setString(3, AccountCycleEnrollmentStatus.PENDING.name());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Failed to enroll account " + accountId + " in cycle " + cycleId);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error enrolling account " + accountId + " in cycle " + cycleId, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public List<CycleWithEnrollmentDto> getCyclesWithEnrollmentsByAccountIdAndStatus(long accountId, String status) {
        String sql = "SELECT c.*, ace.trainer_id AS enrollment_trainer_id, ace.status, ace.created_at AS enrollment_created_at, ace.updated_at AS enrollment_updated_at " +
                "FROM cycles c " +
                "JOIN account_cycle_enrollments ace ON c.id = ace.cycle_id " +
                "WHERE ace.account_id = ?" +
                (status != null ? " AND ace.status = ?" : "");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);

            if (status != null) {
                stmt.setString(2, status);
            }

            rs = stmt.executeQuery();

            List<CycleWithEnrollmentDto> cyclesWithEnrollments = new ArrayList<>();

            while (rs.next()) {
                CycleWithEnrollmentDto cycleWithEnrollment = new CycleWithEnrollmentDto();
                cycleWithEnrollment.setId(rs.getLong("id"));
                cycleWithEnrollment.setName(rs.getString("name"));
                cycleWithEnrollment.setDescription(rs.getString("description"));
                cycleWithEnrollment.setDurationInDays(rs.getInt("duration_in_days"));
                cycleWithEnrollment.setPublished(rs.getBoolean("published"));
                cycleWithEnrollment.setPrice(rs.getBigDecimal("price"));
                cycleWithEnrollment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                cycleWithEnrollment.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                cycleWithEnrollment.setAccountId(accountId);
                cycleWithEnrollment.setStatus(AccountCycleEnrollmentStatus.valueOf(rs.getString("status")));
                cycleWithEnrollment.setTrainerId(rs.getLong("enrollment_trainer_id"));
                cycleWithEnrollment.setEnrollmentCreatedAt(rs.getTimestamp("enrollment_created_at").toLocalDateTime());
                cycleWithEnrollment.setEnrollmentUpdatedAt(rs.getTimestamp("enrollment_updated_at").toLocalDateTime());

                cyclesWithEnrollments.add(cycleWithEnrollment);
            }
            return cyclesWithEnrollments;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching cycles with enrollments for account " + accountId, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void changeCycleStatus(long cycleId, String status) {
        String sql = "UPDATE account_cycle_enrollments SET status = ? WHERE cycle_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, status);
            stmt.setLong(2, cycleId);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("Cycle with id " + cycleId + " not found or status unchanged");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error changing status of cycle with id " + cycleId, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public List<ActiveCycleListDto> getActiveCyclesWithTrainer(int page, int size) {
        String sql = "SELECT c.id, c.name, c.duration_in_days, c.price, ace.trainer_id, trainer.email AS trainer_email, client.id AS client_id, client.email AS client_email, ace.status  " +
                "FROM cycles c " +
                "JOIN account_cycle_enrollments ace ON c.id = ace.cycle_id " +
                "LEFT JOIN accounts trainer ON ace.trainer_id = trainer.id " +
                "JOIN accounts client ON ace.account_id = client.id " +
                "WHERE ace.status = ? " +
                "ORDER BY c.id LIMIT ? OFFSET ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, AccountCycleEnrollmentStatus.ACTIVE.name());
            stmt.setInt(2, size);
            stmt.setInt(3, (page - 1) * size);
            rs = stmt.executeQuery();

            List<ActiveCycleListDto> activeCycles = new ArrayList<>();

            while (rs.next()) {
                ActiveCycleListDto activeCycle = new ActiveCycleListDto();
                activeCycle.setId(rs.getLong("id"));
                activeCycle.setName(rs.getString("name"));
                activeCycle.setDurationInDays(rs.getInt("duration_in_days"));
                activeCycle.setPrice(rs.getBigDecimal("price"));
                activeCycle.setTrainerId(rs.getLong("trainer_id"));
                activeCycle.setTrainerEmail(rs.getString("trainer_email"));
                activeCycle.setClientId(rs.getString("client_id"));
                activeCycle.setClientEmail(rs.getString("client_email"));
                activeCycle.setStatus(AccountCycleEnrollmentStatus.valueOf(rs.getString("status")));

                activeCycles.add(activeCycle);
            }
            return activeCycles;
        } catch (Exception e) {
            throw new RuntimeException("Error fetching active cycles with trainer", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

}
