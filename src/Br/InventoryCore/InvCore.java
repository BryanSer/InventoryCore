/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.InventoryCore;

import java.util.UUID;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-16
 */
public class InvCore {

    public static InventoryData createNewInventory(int rows) {
        UUID uid = UUID.randomUUID();
        InventoryData data = new InventoryData(uid, rows);
        DatabaseHandler.insertInvData(uid.toString(), data);
        return data;
    }

    public static InventoryData getInventory(UUID uid, boolean lock) {
        if (DatabaseHandler.isLocked(uid.toString())) {
            return null;
        }
        if (lock) {
            lock(uid, true);
        }
        return DatabaseHandler.getInvData(uid.toString());
    }

    /**
     *
     * @param inv
     * @param unlock 是否解锁
     * @return 是否更新成功
     */
    public static boolean update(InventoryData inv, boolean unlock) {
        if(isLocked(inv.getID())){
            return false;
        }
        DatabaseHandler.updateInvData(inv.getID().toString(), inv);
        if (unlock) {
            lock(inv.getID(), false);
        }
        return true;
    }

    public static void lock(UUID uid, boolean lock) {
        DatabaseHandler.lock(uid.toString(), lock);
    }
    
    public static boolean isLocked(UUID uid){
        return DatabaseHandler.isLocked(uid.toString());
    }

    private InvCore() {
    }
}
