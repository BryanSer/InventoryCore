/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.InventoryCore;

import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-16
 */
public class InvCore {

    public static UUID createNewInventory(int rows, Consumer<InventoryData> after) {
        UUID uid = UUID.randomUUID();
        Bukkit.getScheduler().runTaskAsynchronously(Main.Plugin, () -> {
            InventoryData data = new InventoryData(uid, rows);
            DatabaseHandler.insertInvData(uid.toString(), data);
            if (after != null) {
                Bukkit.getScheduler().runTask(Main.Plugin, () -> after.accept(data));
            }
        });
        return uid;
    }

    public static void getInventory(UUID uid, boolean lock, Consumer<InventoryData> after) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.Plugin, () -> {
            if (!DatabaseHandler.isLocked(uid.toString())) {
                if (lock) {
                    lock(uid, true);
                }
                Bukkit.getScheduler().runTask(Main.Plugin, () -> after.accept(DatabaseHandler.getInvData(uid.toString())));
            } else {
                Bukkit.getScheduler().runTask(Main.Plugin, () -> after.accept(null));
            }
        });
    }

    /**
     *
     * @param inv
     * @param unlock 是否解锁
     * @return 是否更新成功
     */
    public static void update(InventoryData inv, boolean unlock, boolean force) {
        Bukkit.getScheduler().runTaskAsynchronously(Main.Plugin, () -> {
            if (isLocked(inv.getID()) && !force) {
                return;
            }
            DatabaseHandler.updateInvData(inv.getID().toString(), inv);
            if (unlock) {
                lock(inv.getID(), false);
            }
        });
    }

    public static void lock(UUID uid, boolean lock) {
        DatabaseHandler.lock(uid.toString(), lock);
    }

    public static boolean isLocked(UUID uid) {
        return DatabaseHandler.isLocked(uid.toString());
    }

    private InvCore() {
    }
}
