package top.maplex.slimeEasy.registry

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack
import io.github.thebusybiscuit.slimefun4.api.items.groups.NestedItemGroup
import io.github.thebusybiscuit.slimefun4.api.items.groups.SubItemGroup
import org.bukkit.Material
import org.bukkit.NamespacedKey
import top.maplex.slimeEasy.SlimeEasy
import top.maplex.slimeEasy.config.SEText

/**
 * 物品分类注册中心。
 *
 * 集中管理本附属所有 [ItemGroup], 便于统一维护与查找。
 */
object Groups {

    /**
     * SlimeEasy 主分类。
     *
     * 所有玩家可见的子分类都挂在这里，避免在 Slimefun 主指南中散落多个 SlimeEasy 分类。
     */
    val MAIN: NestedItemGroup = NestedItemGroup(
        NamespacedKey(SlimeEasy.instance, "slimeeasy"),
        SlimefunItemStack("SE_GROUP_MAIN", Material.SLIME_BALL, "&aSlimeEasy")
    )

    /**
     * "实用机械" 分类。
     *
     * 图标使用活塞, 直观体现本分类以机械装置为主。
     * NamespacedKey 的命名空间取插件实例, key 使用小写下划线风格。
     */
    val UTILITY_MACHINES: ItemGroup = SubItemGroup(
        NamespacedKey(SlimeEasy.instance, "utility_machines"),
        MAIN,
        SEText.localized(
            "SE_GROUP_UTILITY_MACHINES",
            Material.PISTON,
            "groups.utility-machines"
        )
    )

    /**
     * "实用工具" 分类。
     *
     * 图标使用铜锄, 呼应本分类以手持勘察 / 辅助工具为主。
     */
    val UTILITY_TOOLS: ItemGroup = SubItemGroup(
        NamespacedKey(SlimeEasy.instance, "utility_tools"),
        MAIN,
        SEText.localized(
            "SE_GROUP_UTILITY_TOOLS",
            Material.COPPER_HOE,
            "groups.utility-tools"
        )
    )

    /**
     * "存储系统" 分类。
     *
     * 图标使用木桶, 涵盖抽屉 / 翻页箱 / 磁盘管理器 / 存储网络与各类升级组件。
     */
    val STORAGE: ItemGroup = SubItemGroup(
        NamespacedKey(SlimeEasy.instance, "storage"),
        MAIN,
        SEText.localized(
            "SE_GROUP_STORAGE",
            Material.BARREL,
            "groups.storage"
        )
    )

    /**
     * "简易村民" 分类。
     *
     * 图标使用村民刷怪蛋, 涵盖村民捕捉器、僵尸信号、交易器、刷铁机、小学、治愈机与遗忘药剂。
     */
    val VILLAGER: ItemGroup = SubItemGroup(
        NamespacedKey(SlimeEasy.instance, "villager"),
        MAIN,
        SEText.localized(
            "SE_GROUP_VILLAGER",
            Material.VILLAGER_SPAWN_EGG,
            "groups.villager"
        )
    )

    /**
     * "简易的领地" 分类。
     *
     * 磁石图标对应领地核心；分类只承载核心与按区块扩展的旗帜，避免与机器分类混杂。
     */
    val TERRITORY: ItemGroup = SubItemGroup(
        NamespacedKey(SlimeEasy.instance, "territory"),
        MAIN,
        SEText.localized(
            "SE_GROUP_TERRITORY",
            Material.LODESTONE,
            "groups.territory"
        )
    )
}
