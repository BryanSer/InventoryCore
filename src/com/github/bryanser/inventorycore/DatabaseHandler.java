/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package com.github.bryanser.inventorycore;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-15
 */
public class DatabaseHandler {

    private static HikariDataSource hikari;

    public static void connect(String host, int port, String database, String user, String password) {
        String connect = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true",
                host, port, database, user, password);
        try {
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(connect);
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.setIdleTimeout(60000);
            config.setConnectionTimeout(60000);
            config.setValidationTimeout(3000);
            config.setMaxLifetime(60000);
            config.setMaximumPoolSize(50);
            hikari = new HikariDataSource(config);
            Connection CONN = hikari.getConnection();

            Statement sta = CONN.createStatement();
            sta.execute("CREATE TABLE IF NOT EXISTS InventoryCore(UUID VARCHAR(100), InvData MEDIUMBLOB, PRIMARY KEY(`UUID`)) ENGINE = InnoDB DEFAULT CHARSET=utf8");
            sta.execute("CREATE TABLE IF NOT EXISTS InvntoryCoreLock(UUID VARCHAR(100), Locked BOOLEAN , PRIMARY KEY(`UUID`)) ENGINE = InnoDB DEFAULT CHARSET=utf8");
            hikari.evictConnection(CONN);

        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void lock(String key, boolean setto) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement lock = CONN.prepareStatement("UPDATE InvntoryCoreLock SET Locked = ? WHERE UUID = ? LIMIT 1");
            lock.setBoolean(1, setto);
            lock.setString(2, key);
            lock.execute();
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean isLocked(String key) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement isLocked = CONN.prepareStatement("SELECT Locked FROM InvntoryCoreLock WHERE UUID = ? LIMIT 1");
            isLocked.setString(1, key);
            ResultSet rs = isLocked.executeQuery();
            if (rs.next()) {
                boolean b = rs.getBoolean(1);
                hikari.evictConnection(CONN);
                return b;
            }
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static void removeInvData(String key) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement removeInvData = CONN.prepareStatement("DELETE FROM InventoryCore WHERE UUID = ? LIMIT 1");
            removeInvData.setString(1, key);
            removeInvData.execute();
            PreparedStatement removeLock = CONN.prepareStatement("DELETE FROM InvntoryCoreLock WHERE UUID = ? LIMIT 1");
            removeLock.setString(1, key);
            removeLock.execute();
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        } finally {

        }
    }

    public static void insertInvData(String key, InventoryData data) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement insertInvData = CONN.prepareStatement("INSERT INTO InventoryCore VALUES (?, ?)");
            insertInvData.setString(1, key);
            insertInvData.setObject(2, data);
            insertInvData.execute();
            PreparedStatement insertLock = CONN.prepareStatement("INSERT INTO InvntoryCoreLock VALUES (?, ?)");
            insertLock.setString(1, key);
            insertLock.setBoolean(2, false);
            insertLock.execute();
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void updateInvData(String key, InventoryData data) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement updateInvData = CONN.prepareStatement("UPDATE InventoryCore SET InvData = ? WHERE UUID = ? LIMIT 1");
            updateInvData.setObject(1, data);
            updateInvData.setString(2, key);
            updateInvData.execute();
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static boolean hasInvData(String key) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement getInvData = CONN.prepareStatement("SELECT InvData FROM InventoryCore WHERE UUID = ? LIMIT 1");
            getInvData.setString(1, key);
            ResultSet rs = getInvData.executeQuery();
            boolean b = rs.next();
            hikari.evictConnection(CONN);
            return b;
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static InventoryData getInvData(String key) {
        try {
            Connection CONN = hikari.getConnection();
            PreparedStatement getInvData = CONN.prepareStatement("SELECT InvData FROM InventoryCore WHERE UUID = ? LIMIT 1");
            getInvData.setString(1, key);
            ResultSet rs = getInvData.executeQuery();
            if (rs.next()) {
                InventoryData id = Tools.getObject(rs, 1, InventoryData.class);
                hikari.evictConnection(CONN);
                return id;
            }
            hikari.evictConnection(CONN);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
