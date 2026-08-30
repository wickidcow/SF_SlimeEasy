<div align="center">

# ⚙️ SlimeEasy — Slimefun Legacy

**Automation, storage, machine diagnostics, villager utilities, fake-player machines, and lightweight territory protection for modern Slimefun servers.**

![Slimefun Legacy](https://img.shields.io/badge/Slimefun-Legacy-6bd425?style=for-the-badge)
![Minecraft 26.2](https://img.shields.io/badge/Minecraft-26.2-62b47a?style=for-the-badge)
![Paper 26.2+](https://img.shields.io/badge/Paper-26.2%2B-blue?style=for-the-badge)
![Java 25](https://img.shields.io/badge/Java-25-orange?style=for-the-badge)
![English First](https://img.shields.io/badge/Language-English%20First-4c8bf5?style=for-the-badge)
![Maintained for AlbionMC.com](https://img.shields.io/badge/Maintained%20for-albionmc.com-7b68ee?style=for-the-badge)

</div>

> [!IMPORTANT]
> **SlimeEasy Legacy is an unofficial community-maintained fork** of [FxRayHughes/SlimeEasy](https://github.com/FxRayHughes/SlimeEasy). This fork keeps the original SlimeEasy machines, item IDs, storage data, and gameplay design while maintaining an **English-first** build for modern Paper and **Slimefun Legacy** servers.

## ✨ What does SlimeEasy add?

SlimeEasy is a large utility addon that combines several systems normally found across separate Slimefun addons.

### ⚙️ Automation & machines

- **Basic Sieve / Reinforced Sieve** — Ex Nihilo-style resource processing with linked sieves, configurable rewards, and `BlockDisplay` animations.
- **Auto Breaker** — piston-driven automatic mining with tool, Fortune, Silk Touch, Unbreaking, inventory-capacity, and protection support.
- **Auto Placer** — sticky-piston-driven automatic block placement with owner-based protection checks.
- **Butcher Machine** — automatically attacks mobs using an internal fake player so Looting, XP, rare drops, and Slimefun custom drops behave like player kills.
- **Auto Clicker** — performs real server-side left/right-click interactions with vanilla blocks, Slimefun blocks, and multiblocks.
- **Quarry** — generates cobblestone and upgradeable Netherrack, End Stone, or Blackstone output from a cobblestone/lava/water structure.
- **Creeper Ward** — prevents natural creeper spawns, pushes creepers out of protected areas, and blocks creeper explosions.

### 🔍 Utility tools

- **Ore Surveyor** — scans the Industrial Miner footprint below a selected location and reports mineable ores and estimated fuel usage.
- **Advanced Ore Surveyor** — switches between 7x7 and 11x11 survey footprints.
- **Engineer Goggles** — displays nearby Slimefun machines, multiblocks, energy state, generation/consumption, and available working-state information.
- **Engineer Night Vision Goggles** — combines machine diagnostics with continuous night vision.
- **Growth Inhibitor** — permanently keeps supported baby mobs young until unlocked.
- **Combat Harness I-IV** — equips Happy Ghasts with configurable Guardian-beam combat.

### 📦 Storage system

SlimeEasy includes a complete high-capacity storage network using long integer item counts rather than vanilla stack limits.

- **Mass Storage Drawer** — very large single-item storage.
- **Paged Storage Box** — high-capacity multi-item storage with expandable pages.
- **Disk Manager** — uses chiseled-bookshelf slots for removable storage disks.
- **1K / 4K / 16K / 64K / 128K / 256K Item Storage Disks**.
- **Storage Network Controller / Connector**.
- **Network Input / Output Ports** with Cargo, active I/O, filters, and face controls.
- **Remote Terminal** with multiple controller bindings.
- **Stack, Experience, Magnet, Void, Extraction, Output, Page, Wise, Remote, and Compression upgrades**.

### 🧑‍🌾 Simple Villagers

- **Villager Catcher** — safely stores Villagers and Zombie Villagers as items while preserving their data.
- **Villager Trader** — exposes a captured villager through a virtual merchant interface and supports automatic restocking.
- **Capsule Iron Farm** — produces iron from a captured villager, Zombie Signal, and food.
- **Villager School** — converts Nitwits into unemployed villagers.
- **Villager Healer** — cures captured Zombie Villagers using Golden Apples.
- **Potion of Forgetting** — removes a villager profession so it can take a new job.

### 🏰 Simple Territory

The built-in lightweight claim system provides:

- 3x3 chunk **Territory Core** claims;
- expandable 3x3 **Territory Flag** coverage;
- member and visitor permissions;
- delegated territory management;
- flag appearance management;
- entry and flight rules;
- Slimefun `ProtectionManager` integration;
- owner-aware checks for SlimeEasy automation.

Territory data is stored independently in `plugins/SlimeEasy/territories.yml`.

## 🧪 Compatibility targets

SlimeEasy Legacy `1.0.1` is maintained for:

| Component | Target |
| --- | --- |
| Minecraft | **26.2** |
| Server | **Paper 26.2+** |
| Java | **25** |
| Primary Slimefun | **Slimefun Legacy** |
| Optional dependency | **DecentHolograms 2.10.1** for Engineer Goggles holograms |
| Default language | **English (`en_US`)** |

SlimeEasy uses Paper's **Mojang-mapped NMS/CraftBukkit interfaces**. It is therefore intentionally Paper-specific and should be rebuilt and reviewed when Paper changes internal signatures. It is **not intended as a Spigot build**.

> [!WARNING]
> Do not use PlugMan, `/reload`, or other hot-unload tools with Slimefun addons. Stop the server normally before replacing SlimeEasy.

## 🛠️ Slimefun Legacy maintenance

The Slimefun Legacy fork currently includes:

- complete English player-facing localization;
- `en_US` as the default and built-in fallback locale;
- the original `zh_CN` locale retained as an optional language;
- English configuration comments, menus, lore, messages, research names, logs, and hologram text;
- modern Paper 26.2 / Mojang-mapped NMS build support inherited from the current upstream codebase;
- the upstream fake-player factory improvements used by the Butcher Machine and Auto Clicker;
- preservation of existing SlimeEasy item IDs, recipes, storage keys, territory data, and machine behavior;
- raw versioned release JAR naming for the Slimefun Legacy addon collection.

The `1.0.1` maintenance line is based on upstream SlimeEasy commit `a0090f0`, whose upstream GitHub Actions build completed successfully on Paper 26.2 / Java 25. The fork's 1.0.1 changes are localization, release packaging, documentation, and maintenance metadata; they do not intentionally alter gameplay logic.

## 📦 Current release

**Version:** `1.0.1`

Release builds use the raw JAR filename:

```text
SF_SlimeEasy1.0.1.jar
```

### Installation

1. Stop the server normally.
2. Back up your worlds, Slimefun data, and `plugins/SlimeEasy/` data.
3. Install **Slimefun Legacy**.
4. Put `SF_SlimeEasy1.0.1.jar` in the server's `plugins/` directory.
5. Install **DecentHolograms 2.10.1** only if you want Engineer Goggles holograms.
6. Start the server and confirm SlimeEasy enables without errors.

No client mod is required.

## 🌐 Language support

SlimeEasy Legacy is English-first.

```yaml
language: en_US
```

Bundled locales:

```text
lang/en_US.yml
lang/zh_CN.yml
```

If a selected locale is missing a key, SlimeEasy falls back to the bundled English text.

`/se reload` with `slimeeasy.admin` reloads runtime configuration and language files. Registered items, categories, research entries, feature toggles, and already-cached menu text may still require a full server restart.

## 🔬 Engineer Goggles API

Other addons can extend Engineer Goggles without directly accessing SlimeEasy's internal scan cache:

```kotlin
EngineerGogglesApi.registerProvider(this) { context, content ->
    if (context.slimefunItem.id.startsWith("MY_ADDON_")) {
        content.details += myLocalizedLine(context.block)
    }
}
```

Custom structures that are not registered through Slimefun's normal block/multiblock registry can also register target providers through the public SlimeEasy goggles API.

## ⚙️ Configuration

The main configuration is generated at:

```text
plugins/SlimeEasy/config.yml
```

Major configurable areas include:

- sieve processing speeds, reward chances, and animations;
- Creeper Ward range and push behavior;
- Butcher Machine range, damage, food conversion, and upgrade limits;
- Auto Clicker speed and per-tick safety limits;
- Quarry rates and overflow behavior;
- Ore Surveyor range and fuel estimates;
- Engineer Goggles range and scan pacing;
- Combat Harness damage/range/cooldown;
- storage capacity, filtering, I/O throughput, and network range;
- villager machine timing and production;
- territory feature registration and research level.

## 🏗️ Building from source

```bash
./gradlew build
```

The Gradle build uses **Java 25** and Paper's Mojang-mapped development bundle. The normal build output is produced under `build/libs/`; release automation renames the installable artifact to the stable Slimefun Legacy format:

```text
SF_SlimeEasy<version>.jar
```

A local Paper test server can be launched with:

```bash
./gradlew runServer
```

## ❤️ Credits & project lineage

- **FxRayHughes** — original creator and primary upstream developer of SlimeEasy.
- **FxRayHughes/SlimeEasy** — upstream source and original machine/storage/territory implementation.
- **Slimefun and addon community contributors** — APIs, fixes, testing, and ecosystem maintenance.
- **wickidcow / Slimefun Legacy** — English localization, modern maintenance packaging, and preservation work for current servers and AlbionMC.com.

This fork intentionally keeps upstream attribution visible. It is not represented as an official release from FxRayHughes, the original Slimefun developers, Slimefun United, Mojang Studios, or Microsoft.

## ⚖️ Independence notice

**NOT AN OFFICIAL MINECRAFT PRODUCT. NOT APPROVED BY OR ASSOCIATED WITH MOJANG OR MICROSOFT.**

SlimeEasy Legacy, Slimefun Legacy, and the upstream projects are independent community projects. Minecraft-related names, brands, and assets remain the property of their respective rights holders.

---

<div align="center">

**⚙️ Keep Slimefun useful. Keep the addons alive.**

</div>
