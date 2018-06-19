package com.jtbdevelopment.TwistedBattleship.factory

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.AbstractMultiPlayerGameFactory
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.factory.GameValidator
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 4/5/2015
 * Time: 2:26 PM
 */
@Component
class TBGameFactory extends AbstractMultiPlayerGameFactory<ObjectId, GameFeature, TBGame> {
    TBGameFactory(
            final List<GameInitializer> gameInitializers,
            final List<GameValidator> gameValidators) {
        super(gameInitializers, gameValidators)
    }

    @Override
    protected TBGame newGame() {
        return new TBGame()
    }

    @Override
    protected void copyFromPreviousGame(final TBGame previousGame, final TBGame newGame) {
        super.copyFromPreviousGame(previousGame, newGame);
        newGame.previousId = (ObjectId) previousGame.id
        if (!newGame.features.contains(GameFeature.CruiseMissileEnabled) &&
                !newGame.features.contains(GameFeature.CruiseMissileDisabled)) {
            newGame.features.add(GameFeature.CruiseMissileDisabled)
        }
    }
}
