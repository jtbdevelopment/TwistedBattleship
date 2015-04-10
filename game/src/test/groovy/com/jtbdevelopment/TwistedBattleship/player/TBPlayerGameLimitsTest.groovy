package com.jtbdevelopment.TwistedBattleship.player

/**
 * Date: 4/9/15
 * Time: 9:45 AM
 */
class TBPlayerGameLimitsTest extends GroovyTestCase {
    TBPlayerGameLimits limits = new TBPlayerGameLimits()

    void testDefaultFree() {
        assert limits.defaultDailyFreeGames == TBPlayerGameLimits.DEFAULT_FREE_GAMES_PER_DAY
    }

    void testDefaultPremium() {
        assert limits.defaultDailyPremiumFreeGames == TBPlayerGameLimits.DEFAULT_PREMIUM_PLAYER_GAMES_PER_DAY
    }
}
