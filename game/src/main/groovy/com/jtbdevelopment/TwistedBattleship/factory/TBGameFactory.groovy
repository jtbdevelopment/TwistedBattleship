package com.jtbdevelopment.TwistedBattleship.factory

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory

/**
 * Date: 4/5/2015
 * Time: 2:26 PM
 */
class TBGameFactory extends AbstractMultiPlayerGameFactory<TBGame, GameFeature> {
    @Override
    protected TBGame newGame() {
        return new TBGame()
    }

    @Override
    protected void copyFromPreviousGame(final TBGame previousGame, final TBGame newGame) {
        newGame.previousId = previousGame.id
    }
}
