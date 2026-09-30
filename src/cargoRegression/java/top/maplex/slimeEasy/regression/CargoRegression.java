package top.maplex.slimeEasy.regression;

import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import me.mrCookieSlime.Slimefun.api.item_transport.ItemTransportFlow;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import top.maplex.slimeEasy.storage.core.ItemCodec;
import top.maplex.slimeEasy.storage.core.ItemKey;
import top.maplex.slimeEasy.storage.upgrade.FilterMode;
import top.maplex.slimeEasy.storage.upgrade.ItemFilter;

/** Run only in a disposable CI server, never in a player's world. */
public final class CargoRegression extends JavaPlugin {
    @Override
    public void onEnable() {
        if (!Boolean.getBoolean("slimeeasy.cargoRegression")) {
            throw new IllegalStateException("This test plugin requires a disposable regression server");
        }
        Bukkit.getScheduler().runTaskLater(this, () -> {
            try {
                check(ItemKey.Companion.of(null) == null, "null item");
                check(ItemKey.Companion.of(new ItemStack(Material.AIR)) == null, "air item");
                verifyKey(new ItemStack(Material.IRON_INGOT, 64));

                ItemStack custom = new SlimefunItemStack(
                        "SE_CARGO_REGRESSION", Material.DIAMOND_PICKAXE, "&aCargo tool", "&7Keep item data");
                var meta = custom.getItemMeta();
                meta.displayName(Component.text("Cargo metadata regression"));
                meta.lore(List.of(Component.text("Persistent item identity")));
                meta.addEnchant(Enchantment.UNBREAKING, 3, true);
                meta.setMaxStackSize(16);
                ((Damageable) meta).setDamage(37);
                meta.getPersistentDataContainer().set(
                        new NamespacedKey(this, "payload"), PersistentDataType.STRING, "disk-and-filter-data");
                custom.setItemMeta(meta);
                custom.setAmount(7);
                verifyKey(custom);
                verifyPort(custom);
                getLogger().info("CARGO_WRAPPER_REGRESSION_PASS");
            } catch (Throwable failure) {
                getLogger().log(Level.SEVERE, "CARGO_WRAPPER_REGRESSION_FAIL", failure);
            } finally {
                Bukkit.shutdown();
            }
        }, 40L);
    }

    private void verifyKey(ItemStack original) {
        ItemStack snapshot = original.clone();
        ItemStackWrapper wrapped = ItemStackWrapper.wrap(original);
        // Negative control: the exact operation from 1.0.4 must be rejected.
        boolean rejected = false;
        try {
            wrapped.clone();
        } catch (UnsupportedOperationException expected) {
            rejected = true;
        }
        check(rejected, "real immutable Cargo wrapper");

        ItemKey plainKey = Objects.requireNonNull(ItemKey.Companion.of(original));
        ItemKey wrappedKey = Objects.requireNonNull(ItemKey.Companion.of(wrapped));
        check(!(wrappedKey.getTemplate() instanceof ItemStackWrapper), "mutable template copy");
        check(wrappedKey.getTemplate().getAmount() == 1, "unit amount");
        check(plainKey.equals(wrappedKey) && wrappedKey.equals(plainKey), "symmetric item identity");
        check(plainKey.hashCode() == wrappedKey.hashCode(), "compatible storage hashes");
        var stored = new HashMap<ItemKey, Long>();
        stored.put(plainKey, 123L);
        check(Objects.equals(stored.get(wrappedKey), 123L), "existing storage lookup");
        check(Objects.equals(original.getItemMeta(), wrappedKey.getTemplate().getItemMeta()), "all item metadata");
        check(original.getMaxStackSize() == wrappedKey.getVanillaMaxStack(), "custom stack limit");
        check(Objects.equals(ItemCodec.INSTANCE.encode(plainKey.getTemplate()),
                ItemCodec.INSTANCE.encode(wrappedKey.getTemplate())), "unchanged persisted encoding");
        ItemStack decoded = ItemCodec.INSTANCE.decode(ItemCodec.INSTANCE.encode(wrappedKey.getTemplate()));
        check(wrappedKey.equals(ItemKey.Companion.of(decoded)), "saved key round trip");

        ItemStack display = wrappedKey.toDisplay(12);
        display.editMeta(meta -> meta.displayName(Component.text("Changed display only")));
        check(wrappedKey.equals(plainKey), "display copy isolation");
        ItemStack different = original.clone();
        different.editMeta(meta -> meta.getPersistentDataContainer().set(
                new NamespacedKey(this, "different"), PersistentDataType.INTEGER, 1));
        check(!wrappedKey.equals(ItemKey.Companion.of(ItemStackWrapper.wrap(different))), "distinct custom data");
        check(original.equals(snapshot), "unchanged source stack");
        check(wrapped.getAmount() == snapshot.getAmount()
                && Objects.equals(wrapped.getItemMeta(), snapshot.getItemMeta()), "unchanged wrapper");
    }

    private void verifyPort(ItemStack item) {
        var world = Bukkit.getWorlds().getFirst();
        var location = world.getSpawnLocation().clone();
        location.setY(world.getMaxHeight() - 2);
        var controller = Slimefun.getDatabaseManager().getBlockDataController();
        var preset = Objects.requireNonNull(Slimefun.getRegistry().getMenuPresets().get("SE_NET_INPUT_PORT"));
        location.getBlock().setType(Material.DROPPER);
        controller.createBlock(location, "SE_NET_INPUT_PORT");
        try {
            var menu = new BlockMenu(preset, location, Bukkit.createInventory(null, 27));
            var filter = ItemFilter.Companion.getEXTRACT();
            var wrapped = ItemStackWrapper.wrap(item);
            filter.setMode(location, FilterMode.BLACKLIST);
            check(Arrays.equals(preset.getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapped),
                    new int[] {13}), "empty blacklist accepts wrapped cargo");
            filter.setMode(location, FilterMode.WHITELIST);
            check(preset.getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapped).length == 0,
                    "empty whitelist rejects cargo");
            check(filter.toggle(location, item), "persist filter entry");
            check(Arrays.equals(preset.getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapped),
                    new int[] {13}), "saved whitelist matches wrapped cargo");
            filter.setMode(location, FilterMode.BLACKLIST);
            check(preset.getSlotsAccessedByItemTransport(menu, ItemTransportFlow.INSERT, wrapped).length == 0,
                    "saved blacklist rejects wrapped cargo");
        } finally {
            controller.removeBlock(location);
            location.getBlock().setType(Material.AIR);
        }
    }

    private static void check(boolean condition, String description) {
        if (!condition) throw new AssertionError(description);
    }
}
