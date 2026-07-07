package me.Erotoro.sleepskip.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import ru.fatumsoft.survivalTweaks.api.SurvivalTweaksApi;
import ru.fatumsoft.survivalTweaks.api.SurvivalTweaksApiProvider;

/**
 * Typed adapter for the SurvivalTweaks player-state API.
 */
final class SurvivalTweaksHook {

    private final Plugin survivalTweaksPlugin;

    SurvivalTweaksHook(Plugin survivalTweaksPlugin) {
        this.survivalTweaksPlugin = survivalTweaksPlugin;
    }

    boolean isAvailable() {
        return survivalTweaksPlugin != null && survivalTweaksPlugin.isEnabled() && resolveApi() != null;
    }

    boolean isAfk(Player player) {
        SurvivalTweaksApi api = resolveApi();
        return api != null && api.isAfk(player.getUniqueId());
    }

    boolean isVanished(Player player) {
        SurvivalTweaksApi api = resolveApi();
        return api != null && api.isVanished(player.getUniqueId());
    }

    private SurvivalTweaksApi resolveApi() {
        SurvivalTweaksApi api = SurvivalTweaksApiProvider.get();
        if (api != null) {
            return api;
        }

        RegisteredServiceProvider<SurvivalTweaksApi> registration = Bukkit.getServicesManager()
                .getRegistration(SurvivalTweaksApi.class);
        return registration == null ? null : registration.getProvider();
    }
}
