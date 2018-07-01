package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.stereotype.Component;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class MovesInitializer implements GameInitializer<TBGame> {
    @Override
    public void initializeGame(final TBGame game) {
        game.setRemainingMoves(game.getFeatures().contains(GameFeature.Single) ? 1 : DefaultGroovyMethods.size(Ship.values()));
        game.setMovesForSpecials(game.getFeatures().contains(GameFeature.PerShip) ? 2 : 1);
    }

    public final int getOrder() {
        return DEFAULT_ORDER;
    }
}
