package com.gym.dao.util;

import com.gym.util.Loggable;
import org.springframework.jdbc.datasource.DataSourceUtils;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public interface JdbcCleanup extends Loggable {

    default void closeQuietly(AutoCloseable resource) {
    
        if (resource != null) {
            try {
                resource.close();
            } catch (Exception e) {
                getLogger().error("Failed to close resource", e);
            }
        }
    }

    default void cleanupResources(ResultSet rs, Statement stmt, Connection conn, DataSource dataSource) {
        closeQuietly(rs);
        closeQuietly(stmt);
        DataSourceUtils.releaseConnection(conn, dataSource); // под Spring
    }
}
