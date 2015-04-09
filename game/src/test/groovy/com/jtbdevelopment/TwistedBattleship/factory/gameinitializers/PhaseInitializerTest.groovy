package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GamePhase
import com.jtbdevelopment.TwistedBattleship.state.TBGame

/**
 * Date: 4/6/15
 * Time: 6:28 PM
 */
class PhaseInitializerTest extends GroovyTestCase {
    PhaseInitializer phaseInitializer = new PhaseInitializer()

    void testInitializeGameFromPhase() {
        TBGame game = new TBGame()
        GamePhase.values().findAll {
            GamePhase it ->
                it != GamePhase.Challenged
        }.each {
            GamePhase it ->
                game.gamePhase = it
                assert game.gamePhase != GamePhase.Challenged
                phaseInitializer.initializeGame(game)
                assert game.gamePhase == GamePhase.Challenged
        }
    }

    void testInitializeGameFromNull() {
        TBGame game = new TBGame()
        game.gamePhase = null
        phaseInitializer.initializeGame(game)
        assert game.gamePhase == GamePhase.Challenged
    }
}
