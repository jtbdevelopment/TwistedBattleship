package com.jtbdevelopment.TwistedBattleship.state

/**
 * Date: 4/2/15
 * Time: 6:42 PM
 */
class TBGameTest extends GroovyTestCase {
    TBGame game = new TBGame()

    void testInitialize() {
        assertNull game.gamePhase
        assert [:] == game.playerDetails
        assert 0 == game.gridSize
        assertNull game.rematchTimestamp
        assertNull game.previousId
        assert "" == game.generalMessage
    }
}
