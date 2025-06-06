package com.gym.dao.impl;

import com.gym.dao.CycleDao;
import com.gym.dao.util.JdbcCleanup;
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
}
