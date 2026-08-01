package me.Erotoro.sleepskip.hooks;

import org.bukkit.entity.Player;
import ru.fatumsoft.morphMob.api.MorphMobApi;
import ru.fatumsoft.morphMob.api.MorphMobProvider;

/** Typed, optional adapter for MorphMob player state. */
final class MorphMobHook {

    private final MorphMobApi api;

    MorphMobHook() {
        this.api = MorphMobProvider.get();
    }

    boolean isAvailable() {
        return api != null;
    }

    boolean isMorphed(Player player) {
        return api != null && api.isMorphed(player.getUniqueId());
    }
}
