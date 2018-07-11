package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.springframework.stereotype.Component;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class CurrentPlayerInitializer implements GameInitializer<TBGame> {
    @Override
    public void initializeGame(final TBGame game) {

        game.setCurrentPlayer(game.getPlayers().get(0).getId());
    }

    public final int getOrder() {
        return DEFAULT_ORDER;
    }
}
