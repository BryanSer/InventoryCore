/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.InventoryCore;

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

    private static Connection CONN;
    private static PreparedStatement getInvData;
    private static PreparedStatement updateInvData;
    private static PreparedStatement insertInvData;
    private static PreparedStatement removeInvData;

    private static PreparedStatement insertLock;
    private static PreparedStatement isLocked;
    private static PreparedStatement lock;
    private static PreparedStatement removeLock;

    public static void connect(String host, int port, String database, String user, String password) {
        String connect = String.format("jdbc:mysql://%s:%d/%s?user=%s&password=%s&useUnicode=true&characterEncoding=utf-8&autoReconnect=true&rewriteBatchedStatements=true",
                host, port, database, user, password);
        try {
            CONN = DriverManager.getConnection(connect);

            Statement sta = CONN.createStatement();
            sta.execute("CREATE TABLE IF NOT EXISTS InventoryCore(UUID VARCHAR(100), InvData MEDIUMBLOB, PRIMARY KEY(`UUID`)) ENGINE = InnoDB DEFAULT CHARSET=utf8");
            sta.execute("CREATE TABLE IF NOT EXISTS InvntoryCoreLock(UUID VARCHAR(100), Locked BOOLEAN , PRIMARY KEY(`UUID`)) ENGINE = InnoDB DEFAULT CHARSET=utf8");
            getInvData = CONN.prepareStatement("SELECT InvData FROM InventoryCore WHERE UUID = ? LIMIT 1");
            updateInvData = CONN.prepareStatement("UPDATE InventoryCore SET InvData = ? WHERE UUID = ? LIMIT 1");
            insertInvData = CONN.prepareStatement("INSERT INTO InventoryCore VALUES (?, ?)");
            removeInvData = CONN.prepareStatement("DELETE FROM InventoryCore WHERE UUID = ? LIMIT 1");

            isLocked = CONN.prepareStatement("SELECT Locked FROM InvntoryCoreLock WHERE UUID = ? LIMIT 1");
            lock = CONN.prepareStatement("UPDATE InvntoryCoreLock SET Locked = ? WHERE UUID = ? LIMIT 1");
            insertLock = CONN.prepareStatement("INSERT INTO InvntoryCoreLock VALUES (?, ?)");
            removeLock = CONN.prepareStatement("DELETE FROM InvntoryCoreLock WHERE UUID = ? LIMIT 1");
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized void lock(String key, boolean setto) {
        try {
            lock.setBoolean(1, setto);
            lock.setString(2, key);
            lock.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static synchronized boolean isLocked(String key) {
        try {
            isLocked.setString(1, key);
            ResultSet rs = isLocked.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static synchronized void removeInvData(String key) {
        try {
            removeInvData.setString(1, key);
            removeInvData.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            removeLock.setString(1, key);
            removeLock.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized void insertInvData(String key, InventoryData data) {
        try {
            insertInvData.setString(1, key);
            insertInvData.setObject(2, data);
            insertInvData.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            insertLock.setString(1, key);
            insertLock.setBoolean(2, false);
            insertLock.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static synchronized void updateInvData(String key, InventoryData data) {
        try {
            updateInvData.setObject(1, data);
            updateInvData.setString(2, key);
            updateInvData.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static synchronized boolean hasInvData(String key) {
        try {
            getInvData.setString(1, key);
            ResultSet rs = getInvData.executeQuery();
            return rs.next();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static synchronized InventoryData getInvData(String key) {
        try {
            getInvData.setString(1, key);
            ResultSet rs = getInvData.executeQuery();
            if (rs.next()) {
                return Tools.getObject(rs, 1, InventoryData.class);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
}
