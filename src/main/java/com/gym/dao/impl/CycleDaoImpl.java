package com.gym.dao.impl;

import com.gym.dao.CycleDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.enums.AccountCycleEnrollmentStatus;
import com.gym.model.Cycle;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
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

}
