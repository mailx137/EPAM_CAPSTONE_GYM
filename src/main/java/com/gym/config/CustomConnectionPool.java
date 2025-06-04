package com.gym.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import javax.sql.DataSource;

public class CustomConnectionPool implements DataSource {
    private final BlockingQueue<Connection> pool;
    private final String url;
    private final String username;
    private final String password;
    private final String driverClassName;

    public CustomConnectionPool(String url, String username, String password, String driverClassName,
            int poolSize) throws ClassNotFoundException, SQLException {
        this.url = url;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;

        Class.forName(this.driverClassName);

        pool = new ArrayBlockingQueue<>(poolSize);
        for (int i = 0; i < poolSize; i++) {
            pool.add(createNewConnection());
        }
    }

    private Connection createNewConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection realConnection = pool.take();
            return ConnectionProxyFactory.wrap(realConnection, pool);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for a connection from the pool", e);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("CustomConnectionPool doesn't support different credentials");
    }

    @Override
    public java.io.PrintWriter getLogWriter() {
        return null;
    }

    @Override
    public void setLogWriter(java.io.PrintWriter out) {
    }

    @Override
    public void setLoginTimeout(int seconds) {
    }

    @Override
    public int getLoginTimeout() {
        return 0;
    }

    @Override
    public java.util.logging.Logger getParentLogger() {
        return null;
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return false;
    }
}