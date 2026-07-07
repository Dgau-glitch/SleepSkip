package me.Erotoro.sleepskip.services;

import me.Erotoro.sleepskip.SleepSkip;
import me.Erotoro.sleepskip.util.PlatformScheduler;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Objects;
import java.util.UUID;

/**
 * Keeps Minecraft's native insomnia statistic in sync with SleepSkip-managed rest.
 * <p>
 * Vanilla phantom spawning is driven by {@link Statistic#TIME_SINCE_REST}. Because SleepSkip
 * suppresses the vanilla sleep gamerule and performs its own world transition, the plugin must
 * explicitly mark participating players as rested instead of relying on vanilla's full sleep flow.
 */
public final class PhantomRestService {

    private final SleepSkip plugin;

    public PhantomRestService(SleepSkip plugin) {
        this.plugin = Objects.requireNonNull(plugin, "plugin");
    }

    /** Marks a player as recently rested on that player's owning scheduler/thread. */
    public void resetRestTimer(Player player) {
        if (player == null) {
            return;
        }

        PlatformScheduler.runForPlayer(plugin, player, () -> {
            if (player.isOnline()) {
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }
        });
    }

    /** Marks the supplied sleep participants as recently rested if they are still in the expected world. */
    public void resetRestTimers(World world, Collection<UUID> playerIds) {
        if (world == null || playerIds == null || playerIds.isEmpty()) {
            return;
        }

        for (UUID playerId : playerIds) {
            Player player = Bukkit.getPlayer(playerId);
            resetRestTimerInWorld(player, world);
        }
    }

    private void resetRestTimerInWorld(Player player, World expectedWorld) {
        if (player == null || expectedWorld == null) {
            return;
        }

        PlatformScheduler.runForPlayer(plugin, player, () -> {
            if (player.isOnline() && player.getWorld().equals(expectedWorld)) {
                player.setStatistic(Statistic.TIME_SINCE_REST, 0);
            }
        });
    }
}
