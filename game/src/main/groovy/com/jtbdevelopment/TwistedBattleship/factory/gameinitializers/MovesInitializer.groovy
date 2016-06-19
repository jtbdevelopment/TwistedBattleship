package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
@CompileStatic
class MovesInitializer implements GameInitializer<TBGame> {
    final int order = DEFAULT_ORDER

    @Override
    void initializeGame(final TBGame game) {
        game.remainingMoves = game.features.contains(GameFeature.Single) ? 1 : Ship.values().size()
        game.movesForSpecials = game.features.contains(GameFeature.PerShip) ? 2 : 1
    }
}
