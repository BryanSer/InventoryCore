/*
 * 开发者:Bryan_lzh
 * QQ:390807154
 * 保留一切所有权
 * 若为Bukkit插件 请前往plugin.yml查看剩余协议
 */

package Br.InventoryCore;

import java.io.Serializable;

/**
 *
 * @author Bryan_lzh
 * @version 1.0
 * @since 2018-10-15
 */
public class InventoryData implements Serializable{
    private static final long serialVersionUID = 0x00BACCED;
    
    private String[] Contains = new String[54];
    private int MaxRow;

    public InventoryData(int MaxRow) {
        this.MaxRow = MaxRow;
    }
    
    
    
}
