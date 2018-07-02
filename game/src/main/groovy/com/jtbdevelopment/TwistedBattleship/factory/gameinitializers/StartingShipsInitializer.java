package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
@Component
public class StartingShipsInitializer implements GameInitializer<TBGame> {
    @Override
    public void initializeGame(final TBGame game) {
        //noinspection ConstantConditions
        GameFeature ships = game.getFeatures()
                .stream()
                .filter(f -> f.getGroup().equals(GameFeature.StartingShips))
                .findFirst().get();

        Ship ship = null;
        switch (ships) {
            case StandardShips:
                game.setStartingShips(DefaultGroovyMethods.toList(Ship.values()));
                return;

            case AllCruisers:
                ship = Ship.Cruiser;
                break;
            case AllSubmarines:
                ship = Ship.Submarine;
                break;
            case AllCarriers:
                ship = Ship.Carrier;
                break;
            case AllDestroyers:
                ship = Ship.Destroyer;
                break;
            case AllBattleships:
                ship = Ship.Battleship;
                break;
        }
        game.setStartingShips(Arrays.asList(ship, ship, ship, ship, ship));
    }

    public final int getOrder() {
        return DEFAULT_ORDER;
    }
}
