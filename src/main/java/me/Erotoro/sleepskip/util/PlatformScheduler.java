package me.Erotoro.sleepskip.util;

import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import me.Erotoro.sleepskip.SleepSkip;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

/**
 * Folia 1.21.11+ scheduler facade.
 * <p>
 * Global tasks are only for global state such as time/weather coordination. Player/entity work must
 * go through {@link Player#getScheduler()}. Blocking work must use the async scheduler.
 */
public final class PlatformScheduler {

    private PlatformScheduler() {
    }

    public static void runGlobal(SleepSkip plugin, Runnable runnable) {
        Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
    }

    public static TaskHandle runGlobalDelayed(SleepSkip plugin, Runnable runnable, long delayTicks) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> runnable.run(), delayTicks);
        return task::cancel;
    }

    public static TaskHandle runGlobalAtFixedRate(SleepSkip plugin, Runnable runnable, long delayTicks, long periodTicks) {
        ScheduledTask task = Bukkit.getGlobalRegionScheduler().runAtFixedRate(plugin, scheduledTask -> runnable.run(), delayTicks, periodTicks);
        return task::cancel;
    }

    /** Runs blocking or heavy work off the server tick threads. Never touch Bukkit state here. */
    public static TaskHandle runAsync(SleepSkip plugin, Runnable runnable) {
        ScheduledTask task = Bukkit.getAsyncScheduler().runNow(plugin, scheduledTask -> runnable.run());
        return task::cancel;
    }

    /** Delayed off-thread work. Delay is expressed in ticks for parity with the sync API. */
    public static TaskHandle runAsyncDelayed(SleepSkip plugin, Runnable runnable, long delayTicks) {
        long safeDelayTicks = Math.max(1L, delayTicks);
        ScheduledTask task = Bukkit.getAsyncScheduler().runDelayed(
                plugin,
                scheduledTask -> runnable.run(),
                safeDelayTicks * 50L,
                TimeUnit.MILLISECONDS
        );
        return task::cancel;
    }

    /** Repeating off-thread work. Delay/period are expressed in ticks for parity with the sync API. */
    public static TaskHandle runAsyncAtFixedRate(SleepSkip plugin, Runnable runnable, long initialDelayTicks, long periodTicks) {
        long safeInitialTicks = Math.max(1L, initialDelayTicks);
        long safePeriodTicks = Math.max(1L, periodTicks);
        ScheduledTask task = Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin,
                scheduledTask -> runnable.run(),
                safeInitialTicks * 50L,
                safePeriodTicks * 50L,
                TimeUnit.MILLISECONDS
        );
        return task::cancel;
    }

    public static void runForPlayer(SleepSkip plugin, Player player, Runnable runnable) {
        if (player == null) {
            return;
        }

        player.getScheduler().run(plugin, task -> runnable.run(), null);
    }

    public static void runForPlayerDelayed(SleepSkip plugin, Player player, Runnable runnable, long delayTicks) {
        if (player == null) {
            return;
        }

        player.getScheduler().runDelayed(plugin, task -> runnable.run(), null, delayTicks);
    }

    @FunctionalInterface
    public interface TaskHandle {
        void cancel();
    }
}
