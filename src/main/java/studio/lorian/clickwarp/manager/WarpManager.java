package studio.lorian.clickwarp.manager;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import studio.lorian.clickwarp.data.WarpPoint;
import java.util.*;

//管理Warp点的行为
public class WarpManager {

    // 当前插件实例，用于读取配置文件和输出日志
    private final JavaPlugin plugin;
    //保存所有Warp数据，转换成WarpPoint对象保存到HashMap
    private final Map<String, WarpPoint> points = new HashMap<>();


    public WarpManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    //根据Key获取Warp点
    public WarpPoint getPoint(String key){
        return points.get(key);
    }
    //生成坐标Key
    public String createKey(String world, int x, int y, int z){
        return world + ":" + x + ":" + y + ":" + z;
    }

    //加载Warp配置
    public void loadPoints() {
        //重新读取config.yml
        plugin.reloadConfig();
        //清空旧Warp点
        points.clear();
        //获取config.yml中的Points节点
        ConfigurationSection section = plugin.getConfig().getConfigurationSection("points");

        if (section == null) {
            plugin.getLogger().warning("没有找到points设置！");
            return;
        }

        //遍历所有Warp点
        for (String key : section.getKeys(false)) {
            ConfigurationSection point = section.getConfigurationSection(key);
            if (point == null) continue;

            //读取世界坐标、方块信息
            String world = point.getString("world");
            int x = point.getInt("x");
            int y = point.getInt("y");
            int z = point.getInt("z");
            //不安全的代码Material blockmat = Material.valueOf(point.getString("blockmat"));
            Material blockmat;
            try {
                blockmat = Material.valueOf(point.getString("blockmat"));
            }catch (Exception e) {
                plugin.getLogger().warning("Warp点" + key + "的方块类型错误:" + point.getString("blockmat"));
                continue;}
            //读取Warp名称
            String warp = point.getString("warp");

            //防止没有填写warp
            if (warp == null || warp.isEmpty()) {
                plugin.getLogger().warning("Warp点" + key + "没有设置warp！");
                continue;
            }

            //将配置转换成Java对象
            WarpPoint warpPoint = new WarpPoint(blockmat, warp);

            //添加到缓存列表
            String locationKey = createKey(world, x, y, z);
            points.put(locationKey, warpPoint);

            //输出加载日志
            plugin.getLogger().info("成功加载Warp绑定点:" + locationKey);
        }
    }

    //ListWarp配置
    public Map<String, WarpPoint> allPoints() {
        return Collections.unmodifiableMap(points);
    }

    //按坐标键删除单个绑定点
    public boolean deleteByLocationKey(String locationKey) {
        if (!points.containsKey(locationKey)) {
            return false;
        }
        plugin.getConfig().set("points." + locationKey, null);
        plugin.saveConfig();
        loadPoints();
        return true;
    }

    //按Warp名删除绑定点
    public boolean deleteByWarp(String warp) {
        if (warp == null || warp.isEmpty()) return false;

        boolean deleted = false;
        for (var entry : points.entrySet()) {
            if (warp.equals(entry.getValue().warp())) {
                plugin.getConfig().set("points." + entry.getKey(), null);
                deleted = true;
            }
        }

        if (deleted) {
            plugin.saveConfig();
            loadPoints();
        }
        return deleted;
    }

    //获取所有不重复的Warp名称
    public Set<String> allWarps() {
        Set<String> names = new HashSet<>();
        for (WarpPoint point : points.values()) {
            names.add(point.warp());
        }
        return names;
    }
}
