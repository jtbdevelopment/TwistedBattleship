package com.jtbdevelopment.TwistedBattleship.state

/**
 * Date: 4/2/15
 * Time: 6:42 PM
 */
class TBGameTest extends GroovyTestCase {
    TBGame game = new TBGame()

    void testInitialize() {
        assert [:] == game.playerDetails
        assertEquals([], game.startingShips)
        assertNull(game.currentPlayer)
        assert 0 == game.gridSize
        assertNull game.rematchTimestamp
        assertNull game.previousId
    }
}
