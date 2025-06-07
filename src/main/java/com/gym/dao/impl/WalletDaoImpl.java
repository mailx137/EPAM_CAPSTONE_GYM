package com.gym.dao.impl;

import com.gym.dao.WalletDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.model.Wallet;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class WalletDaoImpl implements WalletDao, JdbcCleanup {
    private DataSource dataSource;

    public WalletDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createWallet(long id) {
        String sql = "INSERT INTO wallets (account_id, balance) VALUES (?, 0.00)";
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error creating wallet for account ID: " + id, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public Optional<Wallet> getWalletByAccountId(long accountId) {
        String sql = "SELECT * FROM wallets WHERE account_id = ?";

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, accountId);

            rs = stmt.executeQuery();
            if (rs.next()) {
                Wallet wallet = new Wallet();
                wallet.setId(rs.getLong("id"));
                wallet.setAccountId(rs.getLong("account_id"));
                wallet.setBalance(rs.getBigDecimal("balance"));
                return Optional.of(wallet);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching wallet for account ID: " + accountId, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void topUp(int amount, long clientId) {
        String sql = "UPDATE wallets SET balance = balance + ? WHERE account_id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, amount);
            stmt.setLong(2, clientId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new RuntimeException("No wallet found for account ID: " + clientId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error topping up wallet for account ID: " + clientId, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }
}
