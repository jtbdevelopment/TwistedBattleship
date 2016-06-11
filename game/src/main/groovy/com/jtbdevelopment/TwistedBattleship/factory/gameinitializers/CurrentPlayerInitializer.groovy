package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
class CurrentPlayerInitializer implements GameInitializer<TBGame> {
    final int order = DEFAULT_ORDER

    @Override
    void initializeGame(final TBGame game) {

        game.currentPlayer = game.players[0].id
    }
}
