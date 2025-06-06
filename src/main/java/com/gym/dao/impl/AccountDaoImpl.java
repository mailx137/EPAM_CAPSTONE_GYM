package com.gym.dao.impl;


import com.gym.dao.AccountDao;
import com.gym.dao.util.JdbcCleanup;
import com.gym.dto.response.AccountWithRolesAndWallet;
import com.gym.model.Account;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class AccountDaoImpl implements AccountDao, JdbcCleanup {
    private DataSource dataSource;

    public AccountDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(Account account) {
        String sql = "INSERT INTO accounts (email, password, email_confirmed, blocked, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";

        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            stmt.setString(1, account.getEmail());
            stmt.setString(2, account.getPassword());
            stmt.setBoolean(3, account.isEmailConfirmed());
            stmt.setBoolean(4, account.isBlocked());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        account.setId(generatedKeys.getLong(1));
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error inserting account: " + account.getEmail(), e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }

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

    @Override
    public boolean emailExists(String mail) {
        String sql = "SELECT EXISTS (SELECT 1 FROM accounts WHERE email = ?)";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, mail);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking if email exists: " + mail, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }

        return false;
    }

    @Override
    public List<Account> getAccountsByPage(int page, int size) {
        String sql = "SELECT * FROM accounts ORDER BY created_at DESC LIMIT ? OFFSET ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            rs = stmt.executeQuery();

            List<Account> accounts = new java.util.ArrayList<>();
            while (rs.next()) {
                Account account = new Account();
                account.setId(rs.getLong("id"));
                account.setEmail(rs.getString("email"));
                account.setPassword(rs.getString("password"));
                account.setEmailConfirmed(rs.getBoolean("email_confirmed"));
                account.setBlocked(rs.getBoolean("blocked"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving accounts by page: " + page, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public int getCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting account count", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
        return 0;
    }

    @Override
    public Optional<AccountWithRolesAndWallet> getAccountWithRolesAndWalletById(long id) {
        String sql = """
                SELECT a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at,
                       w.id AS wallet_id, w.balance AS wallet_balance,
                       GROUP_CONCAT(r.name) AS roles
                FROM accounts a
                LEFT JOIN wallets w ON a.id = w.account_id
                LEFT JOIN accounts_roles ar ON a.id = ar.account_id
                LEFT JOIN roles r ON ar.role_id = r.id
                WHERE a.id = ?
                GROUP BY a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at, 
                         w.id, w.balance
                """;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            rs = stmt.executeQuery();

            if (rs.next()) {
                AccountWithRolesAndWallet account = new AccountWithRolesAndWallet();
                account.setId(rs.getLong("id"));
                account.setEmail(rs.getString("email"));
                account.setEmailConfirmed(rs.getBoolean("email_confirmed"));
                account.setBlocked(rs.getBoolean("blocked"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                Object walletIdObj = rs.getObject("wallet_id");
                Long walletId = walletIdObj == null ? null : ((Number)walletIdObj).longValue();
                account.setWalletId(Optional.ofNullable(walletId));

                account.setWalletBalance(Optional.ofNullable(rs.getBigDecimal("wallet_balance")));

                String roles = rs.getString("roles");
                if (roles != null) {
                    account.setRoles(List.of(roles.split(",")));
                }
                return Optional.of(account);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving account with roles and wallet by id: " + id, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
        return Optional.empty();
    }

    @Override
    public List<AccountWithRolesAndWallet> getAccountsWithRolesAndWallet(int page, int size) {
        String sql = """
                SELECT a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at,
                       w.id AS wallet_id, w.balance AS wallet_balance,
                       GROUP_CONCAT(r.name) AS roles
                FROM accounts a
                LEFT JOIN wallets w ON a.id = w.account_id
                LEFT JOIN accounts_roles ar ON a.id = ar.account_id
                LEFT JOIN roles r ON ar.role_id = r.id
                GROUP BY a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at, 
                         w.id, w.balance
                ORDER BY a.created_at DESC
                LIMIT ? OFFSET ?
                """;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, size);
            stmt.setInt(2, (page - 1) * size);
            rs = stmt.executeQuery();

            List<AccountWithRolesAndWallet> accounts = new ArrayList<>();
            while (rs.next()) {
                AccountWithRolesAndWallet account = new AccountWithRolesAndWallet();
                account.setId(rs.getLong("id"));
                account.setEmail(rs.getString("email"));
                account.setEmailConfirmed(rs.getBoolean("email_confirmed"));
                account.setBlocked(rs.getBoolean("blocked"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                Object walletIdObj = rs.getObject("wallet_id");
                Long walletId = walletIdObj == null ? null : ((Number)walletIdObj).longValue();
                account.setWalletId(Optional.ofNullable(walletId));

                account.setWalletBalance(Optional.ofNullable(rs.getBigDecimal("wallet_balance")));

                String roles = rs.getString("roles");
                if (roles != null) {
                    account.setRoles(List.of(roles.split(",")));
                }
                accounts.add(account);
            }
            return accounts;
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving accounts with roles and wallet", e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }

    @Override
    public void deleteById(long id) {
        String sql = "DELETE FROM accounts WHERE id = ?";
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setLong(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No account found with id: " + id);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting account with id: " + id, e);
        } finally {
            cleanupResources(null, stmt, conn, dataSource);
        }
    }

    @Override
    public Optional<AccountWithRolesAndWallet> getAccountWithRolesAndWalletByEmail(String email) {
        String sql = """
                SELECT a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at,
                       w.id AS wallet_id, w.balance AS wallet_balance,
                       GROUP_CONCAT(r.name) AS roles
                FROM accounts a
                LEFT JOIN wallets w ON a.id = w.account_id
                LEFT JOIN accounts_roles ar ON a.id = ar.account_id
                LEFT JOIN roles r ON ar.role_id = r.id
                WHERE a.email = ?
                GROUP BY a.id, a.email, a.email_confirmed, a.blocked, a.created_at, a.updated_at, 
                         w.id, w.balance
                """;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = DataSourceUtils.getConnection(dataSource);
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            rs = stmt.executeQuery();

            if (rs.next()) {
                AccountWithRolesAndWallet account = new AccountWithRolesAndWallet();
                account.setId(rs.getLong("id"));
                account.setEmail(rs.getString("email"));
                account.setEmailConfirmed(rs.getBoolean("email_confirmed"));
                account.setBlocked(rs.getBoolean("blocked"));
                account.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                account.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());

                Object walletIdObj = rs.getObject("wallet_id");
                Long walletId = walletIdObj == null ? null : ((Number)walletIdObj).longValue();
                account.setWalletId(Optional.ofNullable(walletId));

                account.setWalletBalance(Optional.ofNullable(rs.getBigDecimal("wallet_balance")));

                String roles = rs.getString("roles");
                if (roles != null) {
                    account.setRoles(List.of(roles.split(",")));
                }
                return Optional.of(account);
            }
            return Optional.empty();
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving account with roles and wallet by email: " + email, e);
        } finally {
            cleanupResources(rs, stmt, conn, dataSource);
        }
    }
}
