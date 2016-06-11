package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
class StartingShipsInitializer implements GameInitializer<TBGame> {
    final int order = DEFAULT_ORDER

    @Override
    void initializeGame(final TBGame game) {
        game.startingShips = Ship.values().toList()
    }
}
