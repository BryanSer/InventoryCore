/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package com.github.bryanser.inventorycore;

import Br.API.Utils;
import com.comphenix.protocol.utility.StreamSerializer;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-15
 */
public class Tools {

    public static String serializeItemStack(ItemStack is) {
        try {
            return StreamSerializer.getDefault().serializeItemStack(is);
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ItemStack deserializeItemStack(String str) {
        try {
            return StreamSerializer.getDefault().deserializeItemStack(str);
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T> T Bytes2Object(byte[] b, Class<? extends T> cls) throws IOException,
            ClassNotFoundException {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);
        ObjectInputStream ois = new ObjectInputStream(bais);
        Object obj = ois.readObject();
        return cls.cast(obj);
    }

    public static <T> T getObject(ResultSet sr, String index, Class<? extends T> cls) throws SQLException {
        try {
            return Bytes2Object(sr.getBytes(index), cls);
        } catch (SQLException ex) {
            return sr.getObject(index, cls);
        } catch (IOException ex) {
            return sr.getObject(index, cls);
        } catch (ClassNotFoundException ex) {
            return sr.getObject(index, cls);
        }
    }

    public static <T> T getObject(ResultSet sr, int index, Class<? extends T> cls) throws SQLException {
        try {
            return Bytes2Object(sr.getBytes(index), cls);
        } catch (SQLException ex) {
            return sr.getObject(index, cls);
        } catch (IOException ex) {
            return sr.getObject(index, cls);
        } catch (ClassNotFoundException ex) {
            return sr.getObject(index, cls);
        }
    }
}
