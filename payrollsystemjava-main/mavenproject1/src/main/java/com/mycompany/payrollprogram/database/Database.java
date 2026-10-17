package com.mycompany.payrollprogram.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * JDBC access for the payroll application's login accounts.
 * Uses the MySQL database configured by PAYROLL_DB_URL, PAYROLL_DB_USER,
 * and PAYROLL_DB_PASSWORD. The payrollsystem database must already exist;
 * the users table and demo accounts are created automatically on first login.
 */
public final class Database {

    private static final String DEFAULT_URL =
            "jdbc:mysql://localhost:3306/payrollsystem?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    private static final String DEFAULT_USER = "root";
    private static final int SALT_LENGTH = 16;
    private static final int HASH_BITS = 256;
    private static final int PBKDF2_ITERATIONS = 120_000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static boolean initialized;

    private Database() {
    }

    /**
     * Checks the username and password against the users table.
     * Returns the user's role (ADMIN or EMPLOYEE), or null for invalid credentials.
     */
    public static String authenticate(String username, char[] password) throws SQLException {
        if (username == null || username.trim().isEmpty() || password == null || password.length == 0) {
            return null;
        }

        initialize();

        String sql = "SELECT password_salt, password_hash, `role`, is_active "
                + "FROM users WHERE username = ? LIMIT 1";
        try (Connection connection = openConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username.trim());
            try (ResultSet results = statement.executeQuery()) {
                if (!results.next() || results.getInt("is_active") != 1) {
                    return null;
                }

                byte[] salt = Base64.getDecoder().decode(results.getString("password_salt"));
                byte[] expectedHash = Base64.getDecoder().decode(results.getString("password_hash"));
                byte[] actualHash = derivePasswordHash(password, salt);
                try {
                    return MessageDigest.isEqual(expectedHash, actualHash)
                            ? results.getString("role") : null;
                } finally {
                    Arrays.fill(salt, (byte) 0);
                    Arrays.fill(expectedHash, (byte) 0);
                    Arrays.fill(actualHash, (byte) 0);
                }
            }
        } catch (IllegalArgumentException exception) {
            throw new SQLException("The MySQL payroll database contains an invalid password hash.", exception);
        }
    }

    private static synchronized void initialize() throws SQLException {
        if (initialized) {
            return;
        }

        try (Connection connection = openConnection();
             Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, "
                    + "username VARCHAR(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL UNIQUE, "
                    + "password_salt VARCHAR(64) NOT NULL, "
                    + "password_hash VARCHAR(128) NOT NULL, "
                    + "`role` VARCHAR(20) NOT NULL, "
                    + "is_active TINYINT(1) NOT NULL DEFAULT 1, "
                    + "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, "
                    + "CONSTRAINT chk_users_role CHECK (`role` IN ('ADMIN', 'EMPLOYEE')), "
                    + "CONSTRAINT chk_users_active CHECK (is_active IN (0, 1))"
                    + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci");

            boolean hasUsers;
            try (ResultSet results = statement.executeQuery("SELECT 1 FROM users LIMIT 1")) {
                hasUsers = results.next();
            }
            if (!hasUsers) {
                seedDemoUser(connection, "admin", "admin123", "ADMIN");
                seedDemoUser(connection, "employee", "employee123", "EMPLOYEE");
            }
        }
        initialized = true;
    }

    private static void seedDemoUser(Connection connection, String username, String password, String role)
            throws SQLException {
        char[] passwordChars = password.toCharArray();
        try {
            PasswordCredential credential = createCredential(passwordChars);
            try (PreparedStatement statement = connection.prepareStatement(
                    "INSERT INTO users (username, password_salt, password_hash, `role`, is_active) "
                    + "VALUES (?, ?, ?, ?, 1)")) {
                statement.setString(1, username);
                statement.setString(2, credential.salt());
                statement.setString(3, credential.hash());
                statement.setString(4, role);
                statement.executeUpdate();
            }
        } finally {
            Arrays.fill(passwordChars, '\0');
        }
    }

    private static PasswordCredential createCredential(char[] password) throws SQLException {
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        byte[] hash = derivePasswordHash(password, salt);
        try {
            return new PasswordCredential(
                    Base64.getEncoder().encodeToString(salt),
                    Base64.getEncoder().encodeToString(hash));
        } finally {
            Arrays.fill(salt, (byte) 0);
            Arrays.fill(hash, (byte) 0);
        }
    }

    private static byte[] derivePasswordHash(char[] password, byte[] salt) throws SQLException {
        PBEKeySpec keySpec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(keySpec).getEncoded();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException exception) {
            throw new SQLException("Unable to securely process the login password.", exception);
        } finally {
            keySpec.clearPassword();
        }
    }

    private static Connection openConnection() throws SQLException {
        String url = setting("payroll.db.url", "PAYROLL_DB_URL", DEFAULT_URL);
        String username = setting("payroll.db.user", "PAYROLL_DB_USER", DEFAULT_USER);
        String password = setting("payroll.db.password", "PAYROLL_DB_PASSWORD", "");
        return DriverManager.getConnection(url, username, password);
    }

    private static String setting(String systemProperty, String environmentVariable, String defaultValue) {
        String value = System.getProperty(systemProperty);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        return value == null ? defaultValue : value;
    }

    private record PasswordCredential(String salt, String hash) {
    }
}
