package com.jtbdevelopment.TwistedBattleship.factory

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/5/2015
 * Time: 2:26 PM
 */
@Component
@CompileStatic
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
