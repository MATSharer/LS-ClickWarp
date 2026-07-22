package studio.lorian.clickwarp.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import studio.lorian.clickwarp.data.WarpPoint;
import studio.lorian.clickwarp.manager.WarpManager;
import studio.lorian.clickwarp.manager.WarpSetManager;
import java.util.Map;

public class ClickWarpCommand implements CommandExecutor {
    private final WarpSetManager warpSetManager;
    private final WarpManager warpManager;

    public ClickWarpCommand(WarpSetManager warpSetManager, WarpManager warpManager) {
        this.warpSetManager = warpSetManager;
        this.warpManager = warpManager;
    }

    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        if(!sender.hasPermission("clickwarp.admin")) {
            sender.sendMessage(ChatColor.RED + "你没有权限使用此命令");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp reload");
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp set <warp名称>");
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp unset");
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp delete w:<warp名称>");
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp delete <世界:x:y:z>");
            sender.sendMessage(ChatColor.RED + "请输入: /clickwarp list");
            return true;
        }

        //reload
        if (args[0].equalsIgnoreCase("reload")) {
            warpManager.loadPoints();
            sender.sendMessage(ChatColor.GREEN + "ClickWarp：配置已重新加载");
            return true;
        }

        //set
        if (args[0].equalsIgnoreCase("set")) {
            if(!(sender instanceof Player player)) {
                sender.sendMessage("只有玩家可以使用");
                return true;
            }
            if(args.length < 2) {
                player.sendMessage(ChatColor.RED + "请输入: /clickwarp set <warp名称>");
                return true;
            }

            String warp = args[1];
            warpSetManager.setWarp(player.getUniqueId(), warp);
            player.sendMessage(ChatColor.GREEN + "请右键点击方块绑定Warp点:" + warp);
            return true;
        }

        //unset
        if (args[0].equalsIgnoreCase("unset")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("只有玩家可以使用");
                return true;
            }
            if (!warpSetManager.isSetting(player.getUniqueId())) {
                player.sendMessage(ChatColor.YELLOW + "你当前没有处于Warp绑定模式");
                return true;
            }

            warpSetManager.removeSetWarp(player.getUniqueId());
            player.sendMessage(ChatColor.GREEN + "已退出Warp绑定模式");
            return true;
        }

        //delete
        if (args[0].equalsIgnoreCase("delete")) {
            if (args.length < 2) {
                sender.sendMessage(ChatColor.RED + "用法:");
                sender.sendMessage(ChatColor.RED + "批量删除: /clickwarp delete w:<warp名称>");
                sender.sendMessage(ChatColor.RED + "删除单个: /clickwarp delete <世界:x:y:z>");
                return  true;
            }
            String input = args[1];
            //方式1：按Warp名删除绑定点
            if (input.startsWith("w:")) {
                String warp = input.substring(2);
                if (warp.isEmpty()) {
                    sender.sendMessage(ChatColor.RED + "请输入warp名称，格式: w:<warp名称>");
                    return true;
                }
                boolean deteled = warpManager.deleteByWarp(warp);
                if (deteled) {
                    sender.sendMessage(ChatColor.GREEN + "已删除所有 " + warp + " 的Warp绑定点");
                }else {
                    sender.sendMessage(ChatColor.RED + "没有找到 " + warp + " 的Warp绑定点");
                }
                return true;
            }
            //方式2：按坐标键删除单个绑定点
            if (input.contains(":")) {
                boolean deleted = warpManager.deleteByLocationKey(input);
                if (deleted) {
                    sender.sendMessage(ChatColor.GREEN + "已删除指定位置的绑定点: " + input);
                }else {
                    sender.sendMessage(ChatColor.RED + "指定位置没有找到Warp绑定点: " + input);
                }
                return true;
            }
            // 格式不匹配，提示正确用法
            sender.sendMessage(ChatColor.RED + "格式错误，正确用法:");
            sender.sendMessage(ChatColor.RED + "批量删除: /clickwarp delete w:<warp名称>");
            sender.sendMessage(ChatColor.RED + "删除单个: /clickwarp delete <世界:x:y:z>");
            return true;
        }

        //list
        if (args[0].equalsIgnoreCase("list")) {
            Map<String, WarpPoint> allPoints = warpManager.allPoints();
            if(allPoints.isEmpty()) {
                sender.sendMessage(ChatColor.YELLOW + "当前没有设置任何Warp绑定点");
                return true;
            }

            sender.sendMessage(ChatColor.GOLD + "===当前Warp绑定点===");
            int index = 1;
            for(Map.Entry<String, WarpPoint> entry : allPoints.entrySet()) {
                String locationKey = entry.getKey();
                WarpPoint point = entry.getValue();
                sender.sendMessage(ChatColor.GRAY + String.valueOf(index) + ". " +
                        ChatColor.GREEN + point.warp() +
                        ChatColor.WHITE + " 位于 " +
                        ChatColor.DARK_AQUA + locationKey);
                index++;
            }
            return  true;
        }

        sender.sendMessage(ChatColor.RED + "未知指令");
        return true;
    }
}
