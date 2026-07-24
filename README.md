# LS-ClickWarp
基于方块右键点击触发Warp传送的Spigot/Paper Minecraft服务端插件，支持方块绑定、解绑、列表查看、两种模式删除绑定点，配置持久化存储在config.yml。

## 环境要求
- Java 17+（推荐JDK25）
- 服务端：Spigot / Paper / Purpur 1.16+ 全版本通用
- 依赖：无任何第三方依赖，纯原生BukkitAPI

## 权限节点
仅管理员可用，单个权限全覆盖：
- clickwarp.admin

### 1. 设置绑定点
/clickwarp set <Warp 名称>
- 玩家执行后进入绑定模式，右键任意方块即可将该方块绑定为点击传送点
- 点击方块执行指令：`/warp 你设置的名称`

### 2. 退出绑定模式（unset）
/clickwarp unset
- 中途取消set绑定操作，清空当前玩家设置状态

### 3. 重载配置文件
/clickwarp reload
- 重新读取config.yml绑定数据，刷新内存缓存

### 4. 查看所有已绑定点位 list
/clickwarp list
- 列表格式：`序号. Warp名 | 位于:世界:X:Y:Z`

### 5. 删除绑定点（两种模式，核心功能）
#### ① 批量删除：删除同一个Warp名称所有绑定方块
/clickwarp delete w:<Warp 名称>
- 示例 /clickwarp delete w:spawn
#### ② 精准单个删除：只删除指定坐标方块绑定
/clickwarp delete <世界名:X:Y:Z>
- 示例 /clickwarp delete world:64:65:-20
