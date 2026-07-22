package studio.lorian.clickwarp.data;

import org.bukkit.Material;

// 保存一个点击传送点的数据
public record WarpPoint(Material blockmat, String warp) {}
