package me.Erotoro.sleepskip.hooks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Adapter for SurvivalTweaks AFK state. SleepSkip does not run its own AFK tracker.
 */
final class SurvivalTweaksAfkHook {

    private static final String PROVIDER_CLASS = "ru.fatumsoft.survivaltweaks.api.SurvivalTweaksProvider";

    private final Plugin survivalTweaksPlugin;
    private final BiConsumer<String, String> warnOnce;

    SurvivalTweaksAfkHook(Plugin survivalTweaksPlugin, BiConsumer<String, String> warnOnce) {
        this.survivalTweaksPlugin = survivalTweaksPlugin;
        this.warnOnce = warnOnce;
    }

    boolean isAvailable() {
        return survivalTweaksPlugin != null && survivalTweaksPlugin.isEnabled();
    }

    boolean isAfk(Player player) {
        if (!isAvailable()) {
            return false;
        }

        Object target = resolveApiTarget();
        UUID playerId = player.getUniqueId();
        return Boolean.TRUE.equals(invokeExact(target, "isAfk", new Class<?>[]{Player.class}, player))
                || Boolean.TRUE.equals(invokeExact(target, "isAFK", new Class<?>[]{Player.class}, player))
                || Boolean.TRUE.equals(invokeExact(target, "isAfk", new Class<?>[]{UUID.class}, playerId))
                || Boolean.TRUE.equals(invokeExact(target, "isAFK", new Class<?>[]{UUID.class}, playerId));
    }

    private Object resolveApiTarget() {
        Object api = firstNonNull(
                invoke(survivalTweaksPlugin, "getApi"),
                invoke(survivalTweaksPlugin, "getAPI"),
                invokeStatic(PROVIDER_CLASS, "getApi"),
                invokeStatic(PROVIDER_CLASS, "getAPI")
        );
        return api == null ? survivalTweaksPlugin : api;
    }

    private Object firstNonNull(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private Object invoke(Object target, String methodName) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName);
            return method.invoke(target);
        } catch (NoSuchMethodException exception) {
            return null;
        } catch (ReflectiveOperationException exception) {
            warnOnce.accept(target.getClass().getName() + "#" + methodName,
                    "Failed to invoke hook method " + target.getClass().getName() + "#" + methodName + ": " + exception.getClass().getSimpleName());
            return null;
        }
    }

    private Object invokeExact(Object target, String methodName, Class<?>[] parameterTypes, Object... args) {
        if (target == null) {
            return null;
        }

        try {
            Method method = target.getClass().getMethod(methodName, parameterTypes);
            return method.invoke(target, args);
        } catch (NoSuchMethodException exception) {
            return null;
        } catch (ReflectiveOperationException exception) {
            warnOnce.accept(target.getClass().getName() + "#" + methodName,
                    "Failed to invoke hook method " + target.getClass().getName() + "#" + methodName + ": " + exception.getClass().getSimpleName());
            return null;
        }
    }

    private Object invokeStatic(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getMethod(methodName);
            return method.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException exception) {
            return null;
        } catch (ReflectiveOperationException exception) {
            warnOnce.accept(className + "#" + methodName,
                    "Failed to invoke hook method " + className + "#" + methodName + ": " + exception.getClass().getSimpleName());
            return null;
        }
    }
}
