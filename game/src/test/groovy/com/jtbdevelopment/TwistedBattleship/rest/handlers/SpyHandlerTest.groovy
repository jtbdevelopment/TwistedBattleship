package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame

/**
 * Date: 5/15/15
 * Time: 6:56 AM
 */
class SpyHandlerTest extends GroovyTestCase {
    SpyHandler handler = new SpyHandler()

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }
}
