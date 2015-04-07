package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GamePhase
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import groovy.transform.CompileStatic

/**
 * Date: 4/6/15
 * Time: 6:27 PM
 */
@CompileStatic
class PhaseInitializer implements GameInitializer<TBGame> {
    @Override
    void initializeGame(final TBGame game) {
        game.gamePhase = GamePhase.Challenged
    }
}
