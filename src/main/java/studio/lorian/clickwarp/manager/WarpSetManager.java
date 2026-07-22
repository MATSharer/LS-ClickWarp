package studio.lorian.clickwarp.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

//管理玩家的设置状态
public class WarpSetManager {

    //保存正在设置Warp的玩家
    private final Map<UUID, String> settingPlayers = new HashMap<>();

    //添加玩家设置状态
    public void setWarp(UUID uuid, String warp) {
        settingPlayers.put(uuid, warp);
    }

    //获取玩家当前设置的Warp
    public String getWarp(UUID uuid) {
        return settingPlayers.get(uuid);
    }

    //判断玩家是否正在设置Warp
    public boolean isSetting(UUID uuid) {
        return settingPlayers.containsKey(uuid);
    }

    //移除玩家设置状态
    public void removeSetWarp(UUID uuid) {
        settingPlayers.remove(uuid);
    }
}
