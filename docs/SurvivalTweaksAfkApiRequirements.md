# SurvivalTweaks AFK API requirements for SleepSkip

SleepSkip no longer ships or maintains its own AFK tracker. It delegates AFK state to SurvivalTweaks so one plugin is the source of truth for player inactivity.

The SurvivalTweaks repository/JAR was not available in this environment (`GITHUB_TOKEN` is not set and `../SurvivalTweaks/build/libs/SurvivalTweaks*.jar` is absent), so SleepSkip uses a small reflective adapter and supports the following stable API shapes if SurvivalTweaks exposes them:

```java
boolean isAfk(org.bukkit.entity.Player player);
boolean isAFK(org.bukkit.entity.Player player);
boolean isAfk(java.util.UUID playerId);
boolean isAFK(java.util.UUID playerId);
```

The methods may be exposed directly by the SurvivalTweaks plugin main class or by an API object returned from:

```java
Object getApi();
Object getAPI();
```

A static provider is also supported if SurvivalTweaks adds one:

```java
ru.fatumsoft.survivaltweaks.api.SurvivalTweaksProvider#getApi()
ru.fatumsoft.survivaltweaks.api.SurvivalTweaksProvider#getAPI()
```

## Recommended API to add to SurvivalTweaks

For a compile-time, non-reflective integration, please add a small public API module/JAR containing:

```java
package ru.fatumsoft.survivaltweaks.api;

import org.bukkit.entity.Player;
import java.util.UUID;

public interface SurvivalTweaksApi {
    boolean isAfk(UUID playerId);

    default boolean isAfk(Player player) {
        return isAfk(player.getUniqueId());
    }
}

public final class SurvivalTweaksProvider {
    public static SurvivalTweaksApi getApi();
}
```

Requirements:
- `isAfk` must be non-blocking and safe to call on the player's Folia entity scheduler thread.
- It should return cached in-memory state only; file/DB/network reads must not happen in the call path.
- The API JAR should be published or copied as `../SurvivalTweaks/build/libs/SurvivalTweaks*.jar` so SleepSkip can use the requested `compileOnly(fileTree(...))` dependency.
