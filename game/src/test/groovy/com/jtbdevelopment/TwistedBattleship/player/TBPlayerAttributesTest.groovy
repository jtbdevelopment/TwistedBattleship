package com.jtbdevelopment.TwistedBattleship.player

/**
 * Date: 4/3/15
 * Time: 7:17 PM
 */
class TBPlayerAttributesTest extends GroovyTestCase {
    TBPlayerAttributes attributes = new TBPlayerAttributes()

    void testInitializesToZero() {
        assert attributes.availablePurchasedGames == 0
        assert attributes.freeGamesUsedToday == 0
    }
}
