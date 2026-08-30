package top.maplex.slimeEasy.config

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextDecoration
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.configuration.file.YamlConfiguration
import top.maplex.slimeEasy.SlimeEasy
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets

/** SlimeEasy's standalone localization service. */
object I18n {

    data class Display<T>(val name: T, val lore: List<T>)

    private val ampersandSerializer = LegacyComponentSerializer.legacyAmpersand()
    private val sectionSerializer = LegacyComponentSerializer.legacySection()
    private lateinit var language: YamlConfiguration

    /** Loads the configured language file. Missing files or keys fall back to bundled English. */
    fun load() {
        val plugin = SlimeEasy.instance
        val locale = SEConfig.language
        val fallbackPath = "lang/$DEFAULT_LANGUAGE.yml"
        val fallbackFile = File(plugin.dataFolder, fallbackPath)
        if (!fallbackFile.exists()) {
            plugin.saveResource(fallbackPath, false)
        }

        val bundledFallback = plugin.getResource(fallbackPath)?.use { stream ->
            InputStreamReader(stream, StandardCharsets.UTF_8).use(YamlConfiguration::loadConfiguration)
        } ?: YamlConfiguration.loadConfiguration(fallbackFile)

        val relativePath = "lang/$locale.yml"
        val selectedFile = File(plugin.dataFolder, relativePath)
        if (!selectedFile.exists() && locale != DEFAULT_LANGUAGE) {
            plugin.logger.warning("Language file $relativePath does not exist, falling back to $DEFAULT_LANGUAGE")
        }
        language = YamlConfiguration.loadConfiguration(if (selectedFile.exists()) selectedFile else fallbackFile).apply {
            setDefaults(bundledFallback)
        }
    }

    /** Returns raw text with `&` color codes preserved for SlimefunItemStack and similar APIs. */
    fun raw(key: String, vararg placeholders: Pair<String, Any?>): String =
        format(language.getString(key) ?: missing(key), placeholders)

    /** Reads a list or YAML multiline block while preserving `&` color codes. */
    fun rawList(key: String, vararg placeholders: Pair<String, Any?>): List<String> =
        values(key).map { format(it, placeholders) }

    /** Reads text and converts it to Bukkit `§` legacy color codes. */
    fun text(key: String, vararg placeholders: Pair<String, Any?>): String =
        sectionSerializer.serialize(ampersandSerializer.deserialize(raw(key, *placeholders)))

    /** Reads text as an Adventure Component and disables inherited item italics. */
    fun component(key: String, vararg placeholders: Pair<String, Any?>): Component =
        withoutItalics(ampersandSerializer.deserialize(raw(key, *placeholders)))

    /** Converts an existing `§` legacy string to a non-italic Adventure component. */
    fun legacyComponent(value: String): Component = withoutItalics(sectionSerializer.deserialize(value))

    /** Reads a list or YAML multiline block as non-italic Adventure components. */
    fun components(key: String, vararg placeholders: Pair<String, Any?>): List<Component> =
        rawList(key, *placeholders).map { withoutItalics(ampersandSerializer.deserialize(it)) }

    /** Reads `{base}.name` and multiline `{base}.lore` for Slimefun items. */
    fun rawDisplay(base: String, vararg placeholders: Pair<String, Any?>): Display<String> =
        Display(raw("$base.name", *placeholders), rawList("$base.lore", *placeholders))

    /** Reads `{base}.name` and multiline `{base}.lore` as Adventure components for UI icons. */
    fun componentDisplay(base: String, vararg placeholders: Pair<String, Any?>): Display<Component> =
        Display(
            component("$base.name", *placeholders),
            components("$base.lore", *placeholders)
        )

    private fun values(key: String): List<String> = when {
        language.isList(key) -> language.getStringList(key)
        language.isString(key) -> language.getString(key).orEmpty().split('\n')
        else -> {
            missing(key)
            emptyList()
        }
    }

    private fun format(value: String, placeholders: Array<out Pair<String, Any?>>): String {
        var result = value
        for ((name, replacement) in placeholders) {
            result = result.replace("{$name}", replacement?.toString().orEmpty())
        }
        return result
    }

    private fun missing(key: String): String {
        SlimeEasy.instance.logger.warning("Missing i18n key: $key")
        return key
    }

    /**
     * ItemMeta displays components with inherited italics under vanilla rules unless explicitly disabled.
     * Setting italics to false keeps child colors and decorations while preventing unwanted item-text italics.
     */
    private fun withoutItalics(component: Component): Component =
        component.decoration(TextDecoration.ITALIC, false)

    private const val DEFAULT_LANGUAGE = "en_US"
}
