package com.gym.dao.impl;


import com.gym.dao.AccountDao;
import com.gym.dao.JdbcCleanup;
import com.gym.model.Account;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class AccountDaoImpl implements AccountDao, JdbcCleanup {
    private DataSource dataSource;

    public AccountDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Account account) {

    }

    @Override
    public Optional<Account> findByEmail(String email) {
        String sql = "SELECT * FROM accounts WHERE email = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setEmail(rs.getString("email"));
                account.setPassword(rs.getString("password"));
                account.setEmailConfirmed(rs.getBoolean("email_confirmed"));
                account.setBlocked(rs.getBoolean("blocked"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding account by email: " + email, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }

        return Optional.empty();
    }
}
