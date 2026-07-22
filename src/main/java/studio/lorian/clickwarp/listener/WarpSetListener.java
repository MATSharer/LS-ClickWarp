package studio.lorian.clickwarp.listener;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;
import studio.lorian.clickwarp.manager.WarpManager;
import studio.lorian.clickwarp.manager.WarpSetManager;

//右键绑定Warp点的监听器
public class WarpSetListener implements Listener {
    private final JavaPlugin plugin;
    private final WarpSetManager warpSetManager;
    private final WarpManager warpManager;

    public WarpSetListener(JavaPlugin plugin, WarpSetManager warpSetManager, WarpManager warpManager) {
        this.plugin = plugin;
        this.warpSetManager = warpSetManager;
        this.warpManager = warpManager;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //只允许右键
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK)  return;
        //防止副手触发两次
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        var player = event.getPlayer();
        //判断玩家是否在设置Warp
        if (!warpSetManager.isSetting(player.getUniqueId())) return;

        //防止方块执行原版行为
        event.setCancelled(true);

        var block = event.getClickedBlock();
        if (block == null) return;

        //获取玩家设置的Warp名字
        String warp = warpSetManager.getWarp(player.getUniqueId());
        if (warp == null) return;

        //获取方块信息
        String world = block.getWorld().getName();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();
        String locationKey = warpManager.createKey(world, x, y, z);

        //用缓存检查该方块是否已绑定
        if (warpManager.getPoint(locationKey) != null) {
            player.sendMessage(ChatColor.RED + "该方块已绑定Warp点，请勿重复设置");
            //直接退出绑定模式
            warpSetManager.removeSetWarp(player.getUniqueId());
            return;
        }

        Material mat = block.getType();

        //写入config.yml
        String path = "points." + locationKey;
        plugin.getConfig().set(path + ".world", world);
        plugin.getConfig().set(path + ".x", x);
        plugin.getConfig().set(path + ".y", y);
        plugin.getConfig().set(path + ".z", z);
        plugin.getConfig().set(path + ".blockmat", mat.name());
        plugin.getConfig().set(path + ".warp", warp);

        //保存文件
        plugin.saveConfig();
        //重新加载
        warpManager.loadPoints();
        //退出设置
        warpSetManager.removeSetWarp(player.getUniqueId());

        //提示玩家
        player.sendMessage(ChatColor.GREEN + "成功绑定Warp点: " + warp);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        warpSetManager.removeSetWarp(event.getPlayer().getUniqueId());
    }

}
