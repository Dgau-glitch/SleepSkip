package me.Erotoro.sleepskip.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerStateServiceAfkPolicyTest {

    @Test
    void afkIsAlwaysFalseWhenIgnoreAfkDisabled() {
        assertFalse(PlayerStateService.resolveAfkFlag(false, false));
        assertFalse(PlayerStateService.resolveAfkFlag(false, true));
    }

    @Test
    void afkComesOnlyFromSurvivalTweaksWhenIgnoreAfkEnabled() {
        assertFalse(PlayerStateService.resolveAfkFlag(true, false));
        assertTrue(PlayerStateService.resolveAfkFlag(true, true));
    }
}
