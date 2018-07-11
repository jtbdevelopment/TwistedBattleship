package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.springframework.stereotype.Component;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class BackwardsCompatibilityInitializer implements GameInitializer<TBGame> {
    @Override
    public void initializeGame(final TBGame game) {
        if (!game.getFeatures().contains(GameFeature.CruiseMissileDisabled) &&
                !game.getFeatures().contains(GameFeature.CruiseMissileEnabled)) {
            game.getFeatures().add(GameFeature.CruiseMissileDisabled);
        }


        if (!game.getFeatures().contains(GameFeature.StandardShips) &&
                !game.getFeatures().contains(GameFeature.AllBattleships) &&
                !game.getFeatures().contains(GameFeature.AllSubmarines) &&
                !game.getFeatures().contains(GameFeature.AllDestroyers) &&
                !game.getFeatures().contains(GameFeature.AllCruisers) &&
                !game.getFeatures().contains(GameFeature.AllCarriers)) {
            game.getFeatures().add(GameFeature.StandardShips);
        }

    }

    public final int getOrder() {
        return EARLY_ORDER;
    }
}
