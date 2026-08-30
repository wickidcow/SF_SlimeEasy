package top.maplex.slimeEasy.config

import top.maplex.slimeEasy.SlimeEasy

/**
 * Central SlimeEasy configuration access layer.
 *
 * Runtime values are read from the in-memory Bukkit configuration on demand,
 * so `/se reload` applies them without a restart. Registration-time values
 * such as feature toggles and research levels are read while Slimefun content
 * is registered and therefore require a restart after changes.
 *
 * Persistent keys, serialization formats, data versions, and fixed GUI slot
 * layouts intentionally remain hard-coded because changing them would break
 * stored data or existing inventories.
 */
object SEConfig {

    private val cfg get() = SlimeEasy.instance.config

    /** Reload config.yml from disk for `/se reload`. */
    fun reload() {
        SlimeEasy.instance.reloadConfig()
    }

    // ==================== Common readers ====================

    private fun int(path: String, def: Int, min: Int = Int.MIN_VALUE): Int =
        cfg.getInt(path, def).coerceAtLeast(min)

    private fun long(path: String, def: Long, min: Long = Long.MIN_VALUE): Long =
        cfg.getLong(path, def).coerceAtLeast(min)

    private fun double(path: String, def: Double, min: Double = Double.NEGATIVE_INFINITY): Double =
        cfg.getDouble(path, def).coerceAtLeast(min)

    private fun bool(path: String, def: Boolean): Boolean =
        cfg.getBoolean(path, def)

    val language: String get() = cfg.getString("language", "en_US") ?: "en_US"

    /** Read a seconds setting and convert it to milliseconds, with a 1-second minimum. */
    private fun seconds(path: String, def: Int): Long =
        cfg.getInt(path, def).coerceAtLeast(1).toLong() * 1000L

    // ==================== Shared machine settings ====================

    val machineDefaultInterval: Int get() = int("machine.frequency.default-interval", 1, 1)
    val machineStepPerAngle: Int get() = int("machine.frequency.step-per-angle", 3, 1)

    // ==================== Auto Breaker / Auto Placer ====================

    val autoBreakerEnabled: Boolean get() = bool("auto-breaker.enabled", true)
    val autoBreakerResearch: Int get() = int("auto-breaker.research-level", 10, 0)

    val autoPlacerEnabled: Boolean get() = bool("auto-placer.enabled", true)
    val autoPlacerResearch: Int get() = int("auto-placer.research-level", 10, 0)

    // ==================== Sieves ====================

    val sieveEnabled: Boolean get() = bool("sieve.enabled", true)
    val sieveResearch: Int get() = int("sieve.research-level", 8, 0)
    val reinforcedSieveResearch: Int get() = int("sieve.reinforced-research-level", 16, 0)

    fun sieveChance(input: String, output: String, default: Int): Int =
        int("sieve.chances.$input.$output", default, 0).coerceAtMost(100)

    val sieveBasicChanceMultiplier: Double
        get() = double("sieve.rewards.basic.chance-multiplier", 0.5, 0.0).coerceAtMost(1.0)

    val sieveBasicGuaranteedAmountMultiplier: Double
        get() = double("sieve.rewards.basic.guaranteed-amount-multiplier", 0.5, 0.01).coerceAtMost(1.0)

    fun sieveBasicChanceOverride(input: String, output: String, default: Int): Int =
        int("sieve.rewards.basic.chance-overrides.$input.$output", default, 0).coerceAtMost(100)

    fun sieveRequiredProgress(input: String, default: Int): Int =
        int("sieve.processing.required-actions.$input", default, 1)

    val sieveActionMultiplier: Double
        get() = double("sieve.processing.action-multiplier", 1.5, 0.01)

    val sieveScaffoldingSpeedMultiplier: Double
        get() = double("sieve.processing.scaffolding-speed-multiplier", 1.5, 0.01)

    val sieveMaxLinkedSieves: Int
        get() = int("sieve.processing.max-linked-sieves", 100, 1)

    val sieveMinimumActionIntervalTicks: Long
        get() = long("sieve.processing.minimum-action-interval-ticks", -1L)

    val sieveAnimationEnabled: Boolean
        get() = bool("sieve.animation.enabled", true)

    val sieveInterpolationTicks: Int
        get() = int("sieve.animation.interpolation-ticks", 3, 0).coerceAtMost(59)

    val sieveCompletionAnimationTicks: Int
        get() = int("sieve.animation.completion-ticks", 5, 0).coerceAtMost(59)

    val sieveStartHorizontalScale: Float
        get() = double("sieve.animation.start-horizontal-scale", 0.8, 0.01).coerceAtMost(4.0).toFloat()

    val sieveEndHorizontalScale: Float
        get() = double("sieve.animation.end-horizontal-scale", 0.64, 0.01).coerceAtMost(4.0).toFloat()

    val sieveStartHeightScale: Float
        get() = double("sieve.animation.start-height-scale", 0.8, 0.01).coerceAtMost(4.0).toFloat()

    // ==================== Creeper Ward ====================

    val creeperWardEnabled: Boolean get() = bool("creeper-ward.enabled", true)
    val creeperWardResearch: Int get() = int("creeper-ward.research-level", 10, 0)
    val creeperWardProtectRadius: Int get() = int("creeper-ward.protect-radius-chunks", 1, 0)
    val creeperWardDetectRadius: Double get() = double("creeper-ward.detect-radius", 48.0, 0.0)
    val creeperWardPushStrength: Double get() = double("creeper-ward.push-strength", 0.8, 0.0)
    val creeperWardPushUp: Double get() = double("creeper-ward.push-up", 0.2, 0.0)
    val creeperWardMaxPushAttempts: Int get() = int("creeper-ward.max-push-attempts", 20, 1)
    val creeperWardProtectionTtlMillis: Long get() = seconds("creeper-ward.protection-ttl-seconds", 8)
    val creeperWardMaxScanChunks: Int get() = int("creeper-ward.max-scan-chunks", 16, 1)

    // ==================== Butcher Machine ====================

    val butcherEnabled: Boolean get() = bool("butcher.enabled", true)
    val butcherResearch: Int get() = int("butcher.research-level", 15, 0)
    val butcherBaseSpan: Int get() = int("butcher.base-span", 3, 1)
    val butcherBaseDepth: Int get() = int("butcher.base-depth", 1, 1)
    val butcherDamagePerLevel: Double get() = double("butcher.damage-per-level", 0.5, 0.0)
    val butcherAttacksPerNutrition: Int get() = int("butcher.attacks-per-nutrition", 15, 1)
    val butcherMaxSatiety: Int get() = int("butcher.max-satiety", 100, 1)
    val butcherMaxUpgradeLevel: Int get() = int("butcher.max-upgrade-level", 5, 0)

    // ==================== Auto Clicker ====================

    val autoClickerEnabled: Boolean get() = bool("auto-clicker.enabled", true)
    val autoClickerResearch: Int get() = int("auto-clicker.research-level", 15, 0)
    val autoClickerMinInterval: Double get() = double("auto-clicker.min-interval", 0.05, 0.05)
    val autoClickerMaxInterval: Double get() = double("auto-clicker.max-interval", 40.0, 0.05)
    val autoClickerDefaultInterval: Double get() = double("auto-clicker.default-interval", 4.0, 0.05)
    val autoClickerFineStep: Double get() = double("auto-clicker.fine-step", 0.05, 0.001)
    val autoClickerCoarseStep: Double get() = double("auto-clicker.coarse-step", 0.25, 0.001)
    val autoClickerMaxClicksPerTick: Int get() = int("auto-clicker.max-clicks-per-tick", 20, 1)
    val autoClickerExtractMaxItemsPerTick: Int get() = int("auto-clicker.extract-max-items-per-tick", 64, 1)

    // ==================== Quarry ====================

    val quarryEnabled: Boolean get() = bool("quarry.enabled", true)
    val quarryResearch: Int get() = int("quarry.research-level", 12, 0)
    val quarryBaseIntervalTicks: Int get() = int("quarry.base-interval-ticks", 2, 1)
    val quarryBaseOutput: Int get() = int("quarry.base-output", 1, 1)
    val quarryTier1Output: Int get() = int("quarry.efficiency-output.tier-1", 1, 1)
    val quarryTier2Output: Int get() = int("quarry.efficiency-output.tier-2", 6, 1)
    val quarryTier3Output: Int get() = int("quarry.efficiency-output.tier-3", 12, 1)
    val quarryTier4Output: Int get() = int("quarry.efficiency-output.tier-4", 32, 1)
    val quarryTier5Output: Int get() = int("quarry.efficiency-output.tier-5", 64, 1)
    val quarryDropOverflow: Boolean get() = bool("quarry.drop-overflow", false)

    // ==================== Ore Surveyor ====================

    val surveyRulerEnabled: Boolean get() = bool("survey-ruler.enabled", true)
    val surveyRulerResearch: Int get() = int("survey-ruler.research-level", 10, 0)
    val surveyRulerCooldownTicks: Int get() = int("survey-ruler.cooldown-ticks", 100, 0)
    val surveyMinerRange: Int get() = int("survey-ruler.miner-range", 3, 1)
    val surveyAdvancedMinerRange: Int get() = int("survey-ruler.advanced-miner-range", 5, 1)
    val surveyPerBucket: Int get() = int("survey-ruler.blocks-per-bucket", 96, 1)
    val surveyPerRaw: Int get() = int("survey-ruler.blocks-per-raw-fuel", 128, 1)
    val surveyPerFuel: Int get() = int("survey-ruler.blocks-per-fuel", 256, 1)

    // ==================== Engineer Goggles ====================

    val engineerGogglesEnabled: Boolean get() = bool("engineer-goggles.enabled", true)
    val engineerGogglesResearch: Int get() = int("engineer-goggles.research-level", 12, 0)
    val engineerGogglesRadius: Int get() = int("engineer-goggles.radius", 16, 1)
    val engineerGogglesRefreshTicks: Long get() = long("engineer-goggles.refresh-ticks", 10L, 1L)
    val engineerGogglesMaxNewCellsPerRefresh: Int
        get() = int("engineer-goggles.max-new-cells-per-refresh", 4, 1)

    // ==================== Growth Inhibitor ====================

    val growthInhibitorEnabled: Boolean get() = bool("growth-inhibitor.enabled", true)
    val growthInhibitorResearch: Int get() = int("growth-inhibitor.research-level", 10, 0)

    // ==================== Combat Harness ====================

    val combatHarnessEnabled: Boolean get() = bool("combat-harness.enabled", true)
    val combatHarnessResearch: Int get() = int("combat-harness.research-level", 20, 0)
    val harnessDamageI: Double get() = double("combat-harness.damage.tier-1", 5.0, 0.0)
    val harnessDamageII: Double get() = double("combat-harness.damage.tier-2", 10.0, 0.0)
    val harnessDamageIII: Double get() = double("combat-harness.damage.tier-3", 20.0, 0.0)
    val harnessDamageIV: Double get() = double("combat-harness.damage.tier-4", 25.0, 0.0)
    val harnessRange: Double get() = double("combat-harness.range", 16.0, 0.0)
    val harnessCooldownMillis: Long get() = long("combat-harness.cooldown-millis", 1500L, 0L)
    val harnessPeriodTicks: Long get() = long("combat-harness.period-ticks", 15L, 1L)

    // ==================== Storage System ====================

    val storageDrawerEnabled: Boolean get() = bool("storage.drawer.enabled", true)
    val storageDrawerResearch: Int get() = int("storage.drawer.research-level", 8, 0)
    val storageDrawerMagnetRadius: Double get() = double("storage.drawer.magnet-radius", 6.0, 0.0)
    val storageDrawerSlots: Int get() = int("storage.drawer.slots", 32, 1)

    val storageBoxEnabled: Boolean get() = bool("storage.box.enabled", true)
    val storageBoxResearch: Int get() = int("storage.box.research-level", 15, 0)
    val storageBoxPageTypes: Int get() = int("storage.box.page-types", 45, 1)
    val storageBoxMagnetRadius: Double get() = double("storage.box.magnet-radius", 6.0, 0.0)

    val storageDiskEnabled: Boolean get() = bool("storage.disk.enabled", true)
    val storageDiskResearch: Int get() = int("storage.disk.research-level", 25, 0)

    val storageUpgradeEnabled: Boolean get() = bool("storage.upgrade.enabled", true)
    val storageUpgradeResearch: Int get() = int("storage.upgrade.research-level", 20, 0)
    val storageUpgradeMaxSlots: Int get() = int("storage.upgrade.max-slots", 12, 1).coerceAtMost(13)
    val storageUpgradeMaxPages: Int get() = int("storage.upgrade.max-pages", 5, 1)
    val storageFilterMaxItems: Int get() = int("storage.upgrade.filter-max-items", 27, 1).coerceAtMost(27)
    val storageFilterDefaultWhitelist: Boolean get() = bool("storage.upgrade.filter-default-whitelist", false)
    val storageIoPullMaxItemsPerTick: Int get() = int("storage.io.pull-max-items-per-tick", 0, 0)
    val storageIoPushMaxItemsPerTick: Int get() = int("storage.io.push-max-items-per-tick", 0, 0)

    val storageNetworkEnabled: Boolean get() = bool("storage.network.enabled", true)
    val storageNetworkResearch: Int get() = int("storage.network.research-level", 30, 0)
    val storageNetworkRemoteTerminalMaxBindings: Int get() =
        int("storage.network.remote-terminal-max-bindings", 0, 0)

    /**
     * Network scan radius in blocks.
     *
     * Limited to 63 because NetworkScan packs each axis into 7 bits. Larger
     * values could produce key collisions and incorrect network topology.
     */
    val storageNetworkScanRadius: Int get() = int("storage.network.scan-radius", 24, 1).coerceAtMost(63)

    // ==================== Simple Villagers ====================

    val villagerCatcherEnabled: Boolean get() = bool("villager.catcher.enabled", true)
    val villagerCatcherResearch: Int get() = int("villager.catcher.research-level", 12, 0)

    val zombieSignalEnabled: Boolean get() = bool("villager.zombie-signal.enabled", true)
    val zombieSignalResearch: Int get() = int("villager.zombie-signal.research-level", 8, 0)

    val villagerTraderEnabled: Boolean get() = bool("villager.trader.enabled", true)
    val villagerTraderResearch: Int get() = int("villager.trader.research-level", 18, 0)
    val traderRestockMillis: Long get() = seconds("villager.trader.restock-interval-seconds", 30)

    val ironFarmEnabled: Boolean get() = bool("villager.iron-farm.enabled", true)
    val ironFarmResearch: Int get() = int("villager.iron-farm.research-level", 20, 0)
    val ironProduceMillis: Long get() = seconds("villager.iron-farm.produce-interval-seconds", 20)
    val ironFoodPerCycle: Int get() = int("villager.iron-farm.food-per-cycle", 1, 0)
    val ironPerCycle: Int get() = int("villager.iron-farm.iron-per-cycle", 1, 1)
    val ironSpeedMaxLevel: Int get() = int("villager.iron-farm.speed-upgrade-max-level", 5, 0)
    val ironSpeedStep: Double get() = double("villager.iron-farm.speed-upgrade-step", 0.5, 0.0)

    val villagerSchoolEnabled: Boolean get() = bool("villager.school.enabled", true)
    val villagerSchoolResearch: Int get() = int("villager.school.research-level", 15, 0)
    val schoolConvertMillis: Long get() = seconds("villager.school.convert-seconds", 30)

    val forgettingPotionEnabled: Boolean get() = bool("villager.forgetting-potion.enabled", true)
    val forgettingPotionResearch: Int get() = int("villager.forgetting-potion.research-level", 12, 0)

    val villagerHealerEnabled: Boolean get() = bool("villager.healer.enabled", true)
    val villagerHealerResearch: Int get() = int("villager.healer.research-level", 15, 0)
    val healerConvertMillis: Long get() = seconds("villager.healer.convert-seconds", 30)

    // ==================== Simple Territory ====================

    val territoryEnabled: Boolean get() = bool("territory.enabled", true)
    val territoryResearch: Int get() = int("territory.research-level", 8, 0)
}
