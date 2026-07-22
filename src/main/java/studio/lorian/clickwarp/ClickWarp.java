package studio.lorian.clickwarp;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import studio.lorian.clickwarp.command.ClickWarpCommand;
import studio.lorian.clickwarp.command.ClickWarpTab;
import studio.lorian.clickwarp.listener.WarpBlockListener;
import studio.lorian.clickwarp.listener.WarpSetListener;
import studio.lorian.clickwarp.manager.WarpManager;
import studio.lorian.clickwarp.manager.WarpSetManager;
import java.util.logging.Logger;

public final class ClickWarp extends JavaPlugin {
    private final Logger logger = getLogger();
    private WarpManager warpManager;
    private WarpSetManager warpSetManager;

    public WarpSetManager getWarpSetManager() {
        return warpSetManager;
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("Loading plugin...");

        saveDefaultConfig();

        //创建并加载传送点管理器
        warpManager = new WarpManager(this);
        warpManager.loadPoints();

        //创建Warp设置状态管理器
        warpSetManager = new WarpSetManager();

        //注册监听器，并把manager传进去
        var pluginManager = Bukkit.getPluginManager();
        pluginManager.registerEvents(new WarpBlockListener(warpManager, warpSetManager), this);
        pluginManager.registerEvents(new WarpSetListener(this, warpSetManager, warpManager), this);

        var clickwarp = getCommand("clickwarp");
        if (clickwarp != null) {
            //注册Tab补全
            clickwarp.setTabCompleter(new ClickWarpTab(warpManager));
            //注册命令
            clickwarp.setExecutor(new ClickWarpCommand(warpSetManager, warpManager));
        }else {
            logger.warning("clickwarp命令注册失败，请检查plugin.yml");
        }

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
