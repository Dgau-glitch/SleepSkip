package me.Erotoro.sleepskip.hooks;

import me.Erotoro.sleepskip.SleepSkip;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.Set;

/**
 * Optional integrations for third-party player-state providers and conflict reporting.
 */
public class ExternalPluginHooks {

    private static final Set<String> CONFLICTING_SLEEP_PLUGINS = Set.of(
            "BetterSleeping4",
            "BetterSleeping",
            "Harbor",
            "SleepMost",
            "NightSkipper"
    );

    private final SleepSkip plugin;
    private final SurvivalTweaksHook survivalTweaksHook;
    private final MorphMobHook morphMobHook;

    public ExternalPluginHooks(SleepSkip plugin) {
        this.plugin = plugin;
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        this.survivalTweaksHook = new SurvivalTweaksHook(pluginManager.getPlugin("SurvivalTweaks"));
        this.morphMobHook = createMorphMobHook(pluginManager.getPlugin("MorphMob"));
    }

    public void logDetectedHooks() {
        if (survivalTweaksHook.isAvailable()) {
            plugin.getLogger().info(plugin.tr(
                    "logs.hook-survivaltweaks",
                    "Hooked into SurvivalTweaks for AFK and vanish detection."
            ));
        } else {
            plugin.getLogger().warning(plugin.tr(
                    "logs.missing-survivaltweaks",
                    "SurvivalTweaks is not installed or enabled; AFK and vanish players will not be excluded by SleepSkip."
            ));
        }
        if (morphMobHook != null && morphMobHook.isAvailable()) {
            plugin.getLogger().info(plugin.tr(
                    "logs.hook-morphmob",
                    "Hooked into MorphMob; morphed players are excluded from sleep calculations."
            ));
        }
    }

    public void logConflicts() {
        PluginManager pluginManager = plugin.getServer().getPluginManager();
        for (String pluginName : CONFLICTING_SLEEP_PLUGINS) {
            Plugin detected = pluginManager.getPlugin(pluginName);
            if (detected != null && detected.isEnabled()) {
                plugin.getLogger().warning(plugin.tr(
                        "logs.sleep-plugin-conflict",
                        "Detected another sleep-related plugin: {plugin}. Behavior conflicts are possible."
                ).replace("{plugin}", pluginName));
            }
        }
    }

    public boolean isVanished(Player player) {
        return survivalTweaksHook.isVanished(player);
    }

    public boolean isAfk(Player player) {
        return survivalTweaksHook.isAfk(player);
    }

    public boolean isMorphed(Player player) {
        return morphMobHook != null && morphMobHook.isMorphed(player);
    }

    private MorphMobHook createMorphMobHook(Plugin morphMobPlugin) {
        if (morphMobPlugin == null || !morphMobPlugin.isEnabled()) {
            return null;
        }

        MorphMobHook hook = new MorphMobHook();
        if (!hook.isAvailable()) {
            plugin.getLogger().warning(plugin.tr(
                    "logs.missing-morphmob-api",
                    "MorphMob is enabled but its API is unavailable; morphed players cannot be excluded."
            ));
        }
        return hook;
    }
}
