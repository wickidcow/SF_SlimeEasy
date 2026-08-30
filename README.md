# SlimeEasy

SlimeEasy is a feature-rich [Slimefun](https://github.com/Slimefun/Slimefun4) addon for modern Paper servers. It adds practical automation, storage, diagnostics, villager utilities, and a lightweight territory/claim system while integrating with Slimefun protections and machine behavior.

This fork uses **English (`en_US`) as the default language**. The original Simplified Chinese locale is retained as an optional language.

## Requirements

| Component | Requirement |
|---|---|
| Server | Paper 26.2+ |
| Java | 25 |
| Required plugin | Slimefun |
| Optional plugin | DecentHolograms 2.10.1 for Engineer Goggles holograms |

SlimeEasy is compiled against Paper's Mojang-mapped NMS/CraftBukkit interfaces and is **not intended for Spigot**. Major Paper updates may require recompilation against the corresponding Paper dev bundle.

The release JAR does not bundle Kotlin. Paper's plugin loader resolves the Kotlin runtime on first startup and then uses its normal dependency cache.

## Download

The `main` branch automatically publishes a rolling development release after a successful build:

```text
https://github.com/wickidcow/SF_SlimeEasy/releases/download/latest/SlimeEasy-1.0-SNAPSHOT.jar
```

Place the JAR in `plugins/` and start the server with Slimefun installed.

## Main Features

SlimeEasy content is organized into five Slimefun categories:

- **Utility Machines**
- **Utility Tools**
- **Storage System**
- **Simple Villagers**
- **Simple Territory**

Feature toggles, research levels, and runtime tuning live in `config.yml`. Player-visible text lives in `lang/<language>.yml`.

### Basic and Reinforced Sieves

Two Slimefun multiblock sieves provide Ex Nihilo-style resource processing.

**Basic Sieve**

- Upward-facing dispenser
- Wooden trapdoor on top
- Right-click the trapdoor to process

**Reinforced Sieve**

- Upward-facing dispenser
- Scaffolding on top
- Wooden trapdoor above the scaffolding
- Processes faster and uses the full configured reward chances

Nearby matching sieves on the same Y level can be advanced together. Processing speed, linked-machine limits, animation behavior, action counts, reward multipliers, and individual drop chances are configurable.

Sieve materials include:

- Dust
- Crushed Netherrack
- Crushed End Stone
- Crushed Blackstone

The sieve animation uses `BlockDisplay` interpolation and particles rather than moving entities every tick.

### Auto Breaker

A piston-driven block breaker that stores drops in its machine inventory.

Features include:

- Tool-aware mining
- Fortune and Silk Touch support
- Vanilla Unbreaking behavior
- Automatic fallback when a tool breaks
- Inventory-capacity protection before breaking blocks
- Protection checks using the real machine owner
- WorldGuard / GriefPrevention-compatible protection flow through Bukkit/Slimefun checks

An item frame with a lever can be used to adjust machine frequency.

### Auto Placer

A sticky-piston-driven block placer that takes blocks from the machine inventory and places them in front of the piston.

The placer uses the real machine owner for protection checks and supports the same adjustable frequency system as the Auto Breaker.

### Creeper Ward

A configurable creeper-protection block.

By default, one ward protects a 3x3 chunk area centered on its chunk:

- Prevents natural creeper spawning in the protected region
- Continuously pushes existing creepers toward the nearest exit
- Prevents creeper explosions inside the protected area
- Removes creepers that remain trapped after repeated push attempts
- Rebuilds protection automatically after restart

Protection radius, detection radius, push force, scan limits, and cache behavior are configurable.

### Butcher Machine

An observer-based mob-processing machine that attacks non-player mobs in the area in front of it.

The menu supports:

- Up to 7 weapons
- Looting / Sharpness / Fire Aspect books
- Food converted into attack capacity
- Range upgrades
- Damage upgrades
- Hopper and Slimefun Cargo input

The machine uses an internal fake player so kills behave much more like real player kills, including XP, rare drops, Looting scaling, and Slimefun custom-drop handling.

The fake player is an internal machine actor only. It does not create a real network connection, join the player list, or behave like a spoofed online player.

Territory and protection checks are always restored to the real machine owner before protected actions are accepted.

### Auto Clicker

A redstone-powered observer machine that performs real server-side left/right-click interactions on the block in front of it.

Features include:

- Independent left-click and right-click toggles
- Configurable click interval from very fast sub-tick rates to slower intervals
- One held-item slot for bone meal, buckets, seeds, and similar interaction items
- Hopper and Cargo refill support
- Optional Extraction Upgrade support
- Vanilla block interaction
- Slimefun single-block interaction
- Slimefun multiblock interaction
- Protection checks using the actual machine owner

### Quarry

A resource generator based on a cobblestone/lava/water structure.

If the cobblestone in front of the Quarry touches both lava and water, the Quarry generates output without consuming the cobblestone.

Supported upgrades include:

- Efficiency I-V
- Netherrack output
- End Stone output
- Blackstone output

Output is pushed directly into nearby compatible containers, drawers, or paged storage boxes.

### Ore Surveyor

A handheld tool that scans the same general mining footprint used by Slimefun Industrial Miners and reports mineable ores below the selected location.

The basic tool uses a 7x7 area. The advanced tool can switch between 7x7 and 11x11 scan sizes.

Display modes:

- Chat report
- Read-only inventory GUI

Reports include ore quantities and configurable Industrial Miner fuel estimates.

### Engineer Goggles

Engineer Goggles display information about nearby Slimefun machines and registered multiblock structures.

They can show:

- Machine/item name
- Stored energy and capacity
- Estimated net energy flow
- Rated energy consumption
- Current generation
- Machine working/idle state when the addon exposes reliable state data

The filter GUI can filter by:

- Slimefun category
- Individual item type
- Addon
- Machine function/type
- Working state

Display modes include:

- Nearby machines
- Aimed target only

DecentHolograms is optional. If it is not installed, SlimeEasy still starts; only the hologram display is unavailable.

#### Engineer Goggles API

Other addons can extend the displayed information without accessing SlimeEasy's internal scan cache:

```kotlin
EngineerGogglesApi.registerProvider(this) { context, content ->
    if (context.slimefunItem.id.startsWith("MY_ADDON_")) {
        content.details += myLocalizedLine(context.block)
    }
}
```

Addons may also register custom machine structures that are not present in Slimefun's normal block or multiblock registry.

### Engineer Night Vision Goggles

Combines Slimefun Night Vision Goggles with Engineer Goggles. The combined item keeps the machine diagnostics/filter behavior while continuously providing night vision when worn.

### Growth Inhibitor

Right-click a baby breedable mob to lock its age and prevent it from growing up. Right-click it again to remove the lock.

Display entities used by SlimeEasy machines are ignored.

### Combat Harness

Four tiers of Happy Ghast harnesses add automatic Guardian-beam combat.

- Attacks hostile mobs in line of sight
- Does not shoot through blocks
- Uses configurable magic damage that ignores armor
- Configurable range, cooldown, and target-scan interval
- Four damage tiers by default: 5 / 10 / 20 / 25

## Storage System

SlimeEasy includes a large-capacity storage system using long integer counts rather than vanilla stack limits.

### Mass Storage Drawer

Stores one item identity in very large quantities.

- Front item display
- Right-click to deposit
- Double-click to deposit matching inventory items
- Left-click to withdraw one
- Shift + Left-click to withdraw a stack
- Upgrade GUI
- Slimefun Cargo support
- Experience-storage mode

### Paged Storage Box

Stores multiple item identities using a paged interface.

- Configurable item types per page
- Multiple expansion pages
- Upgrade GUI
- Cargo support
- High per-type capacity

### Item Storage Disks

The Disk Manager uses the six slots of a chiseled bookshelf to hold and visually display storage disks.

Available capacities:

- 1K
- 4K
- 16K
- 64K
- 128K
- 256K

Disk contents are persisted by UUID through Slimefun data storage, so removing or dropping a disk does not erase its contents.

Each disk supports up to 64 distinct full item identities.

### Storage Network

A Network Controller connects nearby SlimeEasy storage into a single accessible network.

Components include:

- Network Controller
- Network Connector
- Network Input Port
- Network Output Port
- Remote Terminal

The aggregated network terminal combines matching items from all network members into one searchable/sortable interface.

Input/output ports support:

- Cargo mode
- Active adjacent I/O mode
- Combined mode
- Full item-identity blacklist / whitelist filters
- Per-face enable/disable controls

### Storage Upgrades

Available upgrades include:

- Stack Upgrade I / II / III
- Experience Storage Upgrade
- Magnet Upgrade
- Void Upgrade
- Extraction Upgrade
- Item Output Upgrade
- Page Expansion
- Wise Upgrade
- Ender Wise Upgrade
- Remote Upgrade
- Compression Upgrade
- Advanced Compression Upgrade

## Simple Territory

The Territory system provides lightweight chunk claiming that integrates with Slimefun protection checks.

### Territory Core

Placing a Territory Core claims a 3x3 chunk area centered on the core.

- One owned territory per player
- Players may still join multiple other territories
- Right-click to manage the territory

### Territory Flags

Each Territory Flag adds another 3x3 claimed area centered on the flag.

- Up to 35 flags per territory
- Flag areas may overlap
- The complete claim must remain connected to the Territory Core
- Holding a flag displays nearby claimed borders with particles

### Members and Permissions

Per-member permissions include:

- Break blocks
- Place blocks
- Interact with blocks/containers
- PvP
- Attack entities
- Interact with entities

Management delegation can separately allow members to manage:

- Members
- Permissions
- Chunks
- Flag appearance
- Territory settings

Visitors have separate default permissions and entry settings.

Territory checks integrate with Slimefun's `ProtectionManager` and do not intentionally bypass other protection systems such as WorldGuard.

Dynamic territory data is stored in:

```text
plugins/SlimeEasy/territories.yml
```

## Simple Villagers

These machines allow villagers to be safely stored as items and used by automated systems.

### Villager Catcher

Captures a Villager or Zombie Villager while preserving its serialized attributes, profession, level, experience, and trades. Sneak + Right-click releases it.

### Zombie Signal

A reusable catalyst required by the Capsule Iron Farm.

### Villager Trader

Stores a captured villager and workstation and exposes trading through a virtual merchant interface.

When the villager profession matches the installed workstation, trades are automatically restocked at the configured interval.

### Capsule Iron Farm

Produces iron over time when supplied with:

- Villager
- Zombie Signal
- Food

Speed Upgrades reduce the production interval.

### Villager School

Converts a captured Nitwit Villager into an unemployed villager after a configurable delay.

### Potion of Forgetting

Makes a living villager forget its profession and return to an unemployed state.

### Villager Healer

Cures a captured Zombie Villager using a regular Golden Apple after a configurable delay.

## Configuration and Language

Default configuration:

```text
plugins/SlimeEasy/config.yml
```

Default language:

```yaml
language: en_US
```

Bundled locales:

```text
lang/en_US.yml
lang/zh_CN.yml
```

`en_US` is the built-in fallback. If a selected locale is missing a key, SlimeEasy falls back to the bundled English value.

`/se reload` with `slimeeasy.admin` reloads runtime configuration and language files.

Changes that can normally apply immediately include runtime values such as:

- Damage
- Range
- Cooldowns
- Intervals
- Output rates
- I/O throughput
- Limits

Changes to registered content such as feature toggles, research levels, registered item text, categories, or already-cached menu content may require a full server restart.

Do **not** use PlugMan or `/reload` to hot-unload Slimefun addons.

## Building from Source

```bash
./gradlew build
```

The plugin JAR is written to:

```text
build/libs/SlimeEasy-<version>.jar
```

A local Paper test server can be launched with:

```bash
./gradlew runServer
```

## Project Structure

```text
src/main/kotlin/top/maplex/slimeEasy/
├── api/goggles/        Engineer Goggles extension API
├── config/             Configuration and i18n
├── feature/            Utility tools and standalone features
├── machine/            Breaker, placer, butcher, clicker, quarry
├── registry/           Slimefun items, groups, research, recipes
├── storage/            Drawers, boxes, disks, networks, upgrades
├── territory/          Claim model, persistence, permissions, menus
├── villager/           Catcher, trader, iron farm, school, healer
└── util/               Shared helpers

src/main/resources/
├── config.yml
├── paper-plugin.yml
└── lang/
    ├── en_US.yml
    └── zh_CN.yml
```

## CI / Development Builds

Every push is built by GitHub Actions. Successful `main` builds update the rolling `latest` prerelease automatically.

## Credits

SlimeEasy was originally developed by [FxRayHughes](https://github.com/FxRayHughes/SlimeEasy). This fork maintains its own changes and English localization.
