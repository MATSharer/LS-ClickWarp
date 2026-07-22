package studio.lorian.clickwarp.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import studio.lorian.clickwarp.data.WarpPoint;
import studio.lorian.clickwarp.manager.WarpManager;
import studio.lorian.clickwarp.manager.WarpSetManager;

//右键传送玩家的监听器
public class WarpBlockListener implements Listener {
    private final WarpManager warpManager;
    private final WarpSetManager warpSetManager;

    public WarpBlockListener(WarpManager warpManager, WarpSetManager warpSetManager) {
        this.warpManager = warpManager;
        this.warpSetManager = warpSetManager;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPlayerInteract(PlayerInteractEvent event) {
        //只允许右键
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        //防止副手触发两次
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;
        //获取玩家
        var player = event.getPlayer();
        //如果玩家正在设置Warp，不执行传送
        if (warpSetManager.isSetting(player.getUniqueId())) {
            return;
        }
        //获取方块
        var block = event.getClickedBlock();
        //忽略空气方块
        if (block == null) return;

        //根据方块位置生成Key
        String key = warpManager.createKey(block.getWorld().getName(), block.getX(), block.getY(), block.getZ());
        //根据Key直接获取Warp点
        WarpPoint point = warpManager.getPoint(key);
        //没有对应Warp点
        if (point == null) return;
        //检查方块类型
        if (block.getType() != point.blockmat()) return;

        //阻止原版右键行为
        event.setCancelled(true);
        //执行warp命令
        player.performCommand("warp " + point.warp());
    }
}
