package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
@CompileStatic
class BackwardsCompatibilityInitializer implements GameInitializer<TBGame> {
    final int order = EARLY_ORDER

    @Override
    void initializeGame(final TBGame game) {
        if (!game.features.contains(GameFeature.CruiseMissileDisabled) &&
                !game.features.contains(GameFeature.CruiseMissileEnabled)) {
            game.features.add(GameFeature.CruiseMissileDisabled)
        }

        if (!game.features.contains(GameFeature.StandardShips) &&
                !game.features.contains(GameFeature.AllBattleships) &&
                !game.features.contains(GameFeature.AllSubmarines) &&
                !game.features.contains(GameFeature.AllDestroyers) &&
                !game.features.contains(GameFeature.AllCruisers) &&
                !game.features.contains(GameFeature.AllCarriers)) {
            game.features.add(GameFeature.StandardShips)
        }
    }
}
