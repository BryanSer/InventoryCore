/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */
package Br.InventoryCore;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.BiPredicate;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-15
 */
public class InventoryData implements Serializable {

    private static final long serialVersionUID = 0x00BACCED;

    private String[] Contains = new String[54];
    private int MaxRow;
    private UUID ID;

    public InventoryData(UUID id,int MaxRow) {
        this.ID = id;
        this.MaxRow = MaxRow;
    }
    
    public ItemStack geteItem(int slot){
        String s = Contains[slot];
        return s == null ? null : Tools.deserializeItemStack(s);
    }
    
    public void update(int slot,ItemStack is){
        Contains[slot] = is == null ? null : Tools.serializeItemStack(is);
    }

    public Inventory toInventory(InventoryHolder p, String title) {
        Inventory inv = Bukkit.createInventory(p, MaxRow * 9, title);
        for (int i = 0; i < Contains.length; i++) {
            String s = Contains[i];
            if (s == null) {
                continue;
            }
            ItemStack is = Tools.deserializeItemStack(s);
            if (is != null) {
                inv.setItem(i, is);
            }
        }
        return inv;
    }

    public void updateFromInventory(Inventory inv,BiPredicate<Integer,ItemStack> pat) {
        if (inv.getSize() != this.MaxRow * 9) {
            throw new IllegalArgumentException("§c更新用的背包视图非本对象派生");
        }
        for (int i = 0; i < this.MaxRow * 9; i++) {
            ItemStack is = inv.getItem(i);
            if (is == null) {
                Contains[i] = null;
            } else {
                if(pat != null && !pat.test(i, is)){
                    continue;
                }
                Contains[i] = Tools.serializeItemStack(is);
            }
        }
    }

    public UUID getID() {
        return ID;
    }

}
