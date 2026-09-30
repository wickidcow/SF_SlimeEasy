package top.maplex.slimeEasy.regression;

import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.block.data.FaceAttachable;
import org.bukkit.block.data.type.Switch;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.java.JavaPlugin;
import top.maplex.slimeEasy.machine.butcher.FakePlayerFactory;
import top.maplex.slimeEasy.machine.butcher.ButcherLogic;
import top.maplex.slimeEasy.machine.clicker.BlockInteractor;
import top.maplex.slimeEasy.territory.BannerNmsBridge;

/** Native behavior checks executed only by the disposable regression server. */
final class NativeRuntimeRegression {
    private NativeRuntimeRegression() {}

    static void verify(JavaPlugin plugin) throws Exception {
        var world = Bukkit.getWorlds().getFirst();
        Set<UUID> operators = operatorIds();
        Path operatorFile = Path.of("ops.json");
        byte[] originalOps = Files.exists(operatorFile) ? Files.readAllBytes(operatorFile) : null;
        Files.write(Path.of("profile-research-expected.txt"),
                io.github.thebusybiscuit.slimefun4.implementation.Slimefun.getRegistry().getResearches().stream()
                        .filter(io.github.thebusybiscuit.slimefun4.api.researches.Research::isEnabled)
                        .map(research -> research.getKey().toString()).sorted().toList());
        Player fake = FakePlayerFactory.INSTANCE.get(world);
        check(fake != null, "Native fake player must be constructed, not silently disabled");
        check(fake == FakePlayerFactory.INSTANCE.get(world), "Same-world fake player identity");
        check(FakePlayerFactory.INSTANCE.isFake(fake), "Identity-based fake-player recognition");
        check(!Bukkit.getOnlinePlayers().contains(fake), "Fake player must not join online players");
        check(fake.getUniqueId().equals(UUID.nameUUIDFromBytes("SlimeEasyButcherFakePlayer".getBytes(java.nio.charset.StandardCharsets.UTF_8))), "Stable historical fake-player UUID");
        check(fake.getName().equals("SE_Butcher"), "Historical machine-player name");
        plugin.getLogger().info("SLIMEEASY_MACHINE_PROFILE_NAMES current=" + fake.getName()
                + " offline=" + Bukkit.getOfflinePlayer(fake.getUniqueId()).getName());
        fake.setOp(false);
        check(fake.isOp(), "Virtual operator state survives setOp(false)");
        fake.setOp(true);
        var handle = ((CraftPlayer) fake).getHandle();
        check(handle.getBukkitEntity() == fake && handle.getBukkitEntityRaw() == fake, "Both native wrapper paths return one player");
        check(handle.permissions() == LevelBasedPermissionSet.OWNER, "Unchanged native owner permissions");
        for (var method : fake.getClass().getMethods()) {
            if (method.getName().equals("spawnParticle")) {
                check(!Modifier.isAbstract(method.getModifiers()), "Server particle overloads must remain implemented");
            }
        }
        var position = world.getSpawnLocation().clone();
        position.setY(world.getMaxHeight() - 4);
        FakePlayerFactory.INSTANCE.positionAt(fake, position);
        check(Math.abs(fake.getLocation().getX() - position.getX()) < 0.001
                && Math.abs(fake.getLocation().getY() - position.getY()) < 0.001, "Native position update");
        ItemStack held = fake.getInventory().getItemInMainHand();
        var support = position.clone().add(2, -1, 0).getBlock();
        var button = support.getRelative(BlockFace.UP);
        var breakable = position.clone().add(3, 0, 0).getBlock();
        var banner = position.clone().add(4, 0, 0).getBlock();
        var bannerSupport = banner.getRelative(BlockFace.DOWN);
        Cow cow = null;
        try {
            fake.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
            support.setType(Material.STONE);
            button.setType(Material.STONE_BUTTON);
            Switch data = (Switch) button.getBlockData();
            data.setAttachedFace(FaceAttachable.AttachedFace.FLOOR);
            button.setBlockData(data);
            check(BlockInteractor.INSTANCE.useItemOn(fake, button, BlockFace.UP), "Native right-click call");
            check(((Switch) button.getBlockData()).isPowered(), "Actual vanilla button activation");
            breakable.setType(Material.DIRT);
            check(BlockInteractor.INSTANCE.destroyBlock(fake, breakable), "Native block destruction");
            check(breakable.getType().isAir(), "Destroyed block becomes air");

            cow = world.spawn(position.clone().add(1, 0, 0), Cow.class);
            cow.setAI(false);
            double health = cow.getHealth();
            fake.getInventory().setItemInMainHand(new ItemStack(Material.DIAMOND_SWORD));
            // Call the production machine API with its actual source block,
            // supplied targets, held equipment, damage, fire and owner arguments.
            ButcherLogic.INSTANCE.performSweep(support, java.util.List.of(cow),
                    new ItemStack(Material.DIAMOND_SWORD), 4.0, 0,
                    "11111111-2222-3333-4444-555555555555");
            check(Math.abs(cow.getHealth() - (health - 4.0)) < 0.0001,
                    "Configured butcher damage must remain exactly four health points");
            check(!cow.getPersistentDataContainer().has(org.bukkit.NamespacedKey.fromString("slimeeasy:butcher_killer"),
                    org.bukkit.persistence.PersistentDataType.STRING),
                    "Surviving targets must not retain a false machine-kill marker");

            bannerSupport.setType(Material.STONE);
            banner.setType(Material.WHITE_BANNER);
            ItemStack template = new ItemStack(Material.WHITE_BANNER);
            BannerMeta meta = (BannerMeta) template.getItemMeta();
            var pattern = new Pattern(DyeColor.RED, PatternType.CROSS);
            meta.addPattern(pattern);
            template.setItemMeta(meta);
            boolean nativeApplied = BannerNmsBridge.INSTANCE.apply(plugin, banner, template, 1);
            plugin.getLogger().info("SLIMEEASY_BANNER_NATIVE_RESULT applied=" + nativeApplied
                    + " type=" + banner.getType() + " count=" + ((Banner) banner.getState()).getPatterns().size());
            // The production operation deliberately includes a Bukkit fallback.
            // Test that complete path rather than requiring its optional first attempt to succeed.
            var sync = top.maplex.slimeEasy.territory.TerritoryService.class.getDeclaredMethod(
                    "synchronizeFlagBlock", top.maplex.slimeEasy.territory.TerritoryBlock.class,
                    DyeColor.class, java.util.List.class);
            sync.setAccessible(true);
            var anchor = new top.maplex.slimeEasy.territory.TerritoryBlock(world.getUID(), banner.getX(), banner.getY(), banner.getZ());
            check(Boolean.TRUE.equals(sync.invoke(top.maplex.slimeEasy.territory.TerritoryService.INSTANCE,
                    anchor, DyeColor.WHITE, java.util.List.of(pattern))), "Complete territory banner synchronization");
            var patterns = ((Banner) banner.getState()).getPatterns();
            check(patterns.size() == 1 && patterns.getFirst().equals(pattern), "Original banner pattern retained");
            check(operatorIds().equals(operators), "Fake-player operations must not alter operators");
            byte[] afterOps = Files.exists(operatorFile) ? Files.readAllBytes(operatorFile) : null;
            check(Arrays.equals(originalOps, afterOps), "ops.json must remain byte-identical");
            plugin.getLogger().info("SLIMEEASY_NATIVE_FLOOR_REGRESSION_PASS");
        } finally {
            fake.getInventory().setItemInMainHand(held);
            if (cow != null) cow.remove();
            button.setType(Material.AIR);
            support.setType(Material.AIR);
            breakable.setType(Material.AIR);
            banner.setType(Material.AIR);
            bannerSupport.setType(Material.AIR);
        }
    }

    private static Set<UUID> operatorIds() {
        return Bukkit.getOperators().stream().map(org.bukkit.OfflinePlayer::getUniqueId).collect(Collectors.toSet());
    }

    private static void check(boolean condition, String detail) {
        if (!condition) throw new AssertionError(detail);
    }
}
