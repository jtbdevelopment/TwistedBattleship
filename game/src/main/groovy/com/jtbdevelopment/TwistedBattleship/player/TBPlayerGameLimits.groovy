package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.player.tracking.PlayerGameLimits
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/9/15
 * Time: 9:44 AM
 */
@Component
@CompileStatic
class TBPlayerGameLimits implements PlayerGameLimits {
    public static final int DEFAULT_FREE_GAMES_PER_DAY = 10;
    public static final int DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY = 25;

    @Override
    int getDefaultDailyFreeGames() {
        return DEFAULT_FREE_GAMES_PER_DAY
    }

    @Override
    int getDefaultDailyPremiumFreeGames() {
        return DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY
    }
}
