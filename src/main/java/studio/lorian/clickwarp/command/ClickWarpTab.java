package studio.lorian.clickwarp.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import studio.lorian.clickwarp.manager.WarpManager;
import java.util.ArrayList;
import java.util.List;

public class ClickWarpTab implements TabCompleter {
    private  final WarpManager warpManager;
    public ClickWarpTab(WarpManager warpManager) {
        this.warpManager = warpManager;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        List<String> result = new ArrayList<>();

        //第一个参数补全
        if (args.length == 1) {
            if ("reload".startsWith(args[0].toLowerCase())) {result.add("reload");}
            if ("set".startsWith(args[0].toLowerCase())) {result.add("set");}
            if ("unset".startsWith(args[0].toLowerCase())) {result.add("unset");}
            if ("delete".startsWith(args[0].toLowerCase())) {result.add("delete");}
            if ("list".startsWith(args[0].toLowerCase())) {result.add("list");}
        }

        //第二个参数补全
        if (args.length == 2 && args[0].equalsIgnoreCase("delete")) {
            String input = args[1];

            //输入 w: 前缀时，补全 warp 名称
            if (input.startsWith("w:")) {
                String prefix = input.substring(2).toLowerCase();
                for (String warp : warpManager.allWarps()) {
                    if (warp.toLowerCase().startsWith(prefix)) {
                        result.add("w:" + warp);
                    }
                }
            }else {
                for (String key : warpManager.allPoints().keySet()) {
                    if (key.toLowerCase().startsWith(input.toLowerCase())) {
                        result.add(key);
                    }
                }
            }
        }

        return result;
    }
}
