package com.quizmaster.model;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

/**
 * Represents a registered user account with secure password hashing.
 */
public class UserAccount {

    private String username;
    private String displayName;
    private String salt;
    private String passwordHash;
    private String createdAt;

    public UserAccount() {}

    public UserAccount(String username, String displayName, String salt, String passwordHash, String createdAt) {
        this.username = username;
        this.displayName = displayName;
        this.salt = salt;
        this.passwordHash = passwordHash;
        this.createdAt = createdAt;
    }

    /**
     * Factory method to create a new user account with a randomly generated salt
     * and SHA-256 password hash.
     */
    public static UserAccount create(String username, String displayName, String rawPassword) {
        String cleanUser = username.trim().toLowerCase();
        String cleanDisplay = displayName.trim().isEmpty() ? username : displayName.trim();
        String salt = generateSalt();
        String hash = hashPassword(rawPassword, salt);
        String created = LocalDateTime.now().toString();

        return new UserAccount(cleanUser, cleanDisplay, salt, hash, created);
    }

    /**
     * Verifies whether the provided raw password matches this account's hash.
     */
    public boolean verifyPassword(String rawPassword) {
        if (salt == null || passwordHash == null) {
            return false;
        }
        String computed = hashPassword(rawPassword, salt);
        return computed.equals(passwordHash);
    }

    private static String generateSalt() {
        SecureRandom sr = new SecureRandom();
        byte[] bytes = new byte[16];
        sr.nextBytes(bytes);
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes(StandardCharsets.UTF_8));
            byte[] hashed = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    // ── Getters and Setters ─────────────────────────────────────────
    public String getUsername()                     { return username; }
    public void setUsername(String username)         { this.username = username; }

    public String getDisplayName()                   { return displayName; }
    public void setDisplayName(String displayName)   { this.displayName = displayName; }

    public String getSalt()                         { return salt; }
    public void setSalt(String salt)                 { this.salt = salt; }

    public String getPasswordHash()                 { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getCreatedAt()                    { return createdAt; }
    public void setCreatedAt(String createdAt)       { this.createdAt = createdAt; }
}
