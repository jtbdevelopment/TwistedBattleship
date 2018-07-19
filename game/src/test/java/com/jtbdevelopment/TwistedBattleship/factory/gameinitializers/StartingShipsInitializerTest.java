package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
public class StartingShipsInitializerTest extends MongoGameCoreTestCase {
    private StartingShipsInitializer initializer = new StartingShipsInitializer();

    @Test
    public void testGetOrder() {
        assertEquals(GameInitializer.DEFAULT_ORDER, initializer.getOrder());
    }

    @Test
    public void testStandardShips() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.StandardShips)));

        initializer.initializeGame(game);
        assertEquals(Arrays.asList(Ship.values()), game.getStartingShips());
    }

    @Test
    public void testAllCarriers() {
        internalTestAllX(GameFeature.AllCarriers, Ship.Carrier);
    }

    @Test
    public void testAllCruisers() {
        internalTestAllX(GameFeature.AllCruisers, Ship.Cruiser);
    }

    @Test
    public void testAllSubmarines() {
        internalTestAllX(GameFeature.AllSubmarines, Ship.Submarine);
    }

    @Test
    public void testAllBattleShips() {
        internalTestAllX(GameFeature.AllBattleships, Ship.Battleship);
    }

    @Test
    public void testallDestroyers() {
        internalTestAllX(GameFeature.AllDestroyers, Ship.Destroyer);
    }

    private void internalTestAllX(GameFeature allX, Ship shipX) {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(allX)));

        initializer.initializeGame(game);
        assertEquals(Arrays.asList(shipX, shipX, shipX, shipX, shipX), game.getStartingShips());
    }
}
