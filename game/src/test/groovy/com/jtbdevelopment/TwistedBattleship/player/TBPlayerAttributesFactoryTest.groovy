package com.jtbdevelopment.TwistedBattleship.player

import com.jtbdevelopment.games.players.GameSpecificPlayerAttributes

/**
 * Date: 4/3/15
 * Time: 7:18 PM
 */
class TBPlayerAttributesFactoryTest extends GroovyTestCase {
    TBPlayerAttributesFactory factory = new TBPlayerAttributesFactory()

    void testNewPlayer() {
        GameSpecificPlayerAttributes attributes = factory.newPlayerAttributes()
        assertNotNull attributes
        assert attributes instanceof TBPlayerAttributes
    }

    void testNewManualPlayer() {
        GameSpecificPlayerAttributes attributes = factory.newManualPlayerAttributes()
        assertNotNull attributes
        assert attributes instanceof TBPlayerAttributes
    }

    void testNewSystemPlayer() {
        assertNull factory.newSystemPlayerAttributes()
    }
}
