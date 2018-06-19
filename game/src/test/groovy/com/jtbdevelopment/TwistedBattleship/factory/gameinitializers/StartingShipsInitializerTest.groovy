package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.junit.Test

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class StartingShipsInitializerTest extends MongoGameCoreTestCase {
    StartingShipsInitializer initializer = new StartingShipsInitializer()

    @Test
    void testGetOrder() {
        assert GameInitializer.DEFAULT_ORDER == initializer.order
    }

    @Test
    void testStandardShips() {
        TBGame game = new TBGame(features: [GameFeature.StandardShips]
        )

        initializer.initializeGame(game)
        assert Ship.values().toList() == game.startingShips
    }

    @Test
    void testAllCarriers() {
        internalTestAllX(GameFeature.AllCarriers, Ship.Carrier)
    }

    @Test
    void testAllCruisers() {
        internalTestAllX(GameFeature.AllCruisers, Ship.Cruiser)
    }

    @Test
    void testAllSubmarines() {
        internalTestAllX(GameFeature.AllSubmarines, Ship.Submarine)
    }

    @Test
    void testAllBattleShips() {
        internalTestAllX(GameFeature.AllBattleships, Ship.Battleship)
    }

    @Test
    void testallDestroyers() {
        internalTestAllX(GameFeature.AllDestroyers, Ship.Destroyer)
    }

    protected void internalTestAllX(GameFeature allX, shipX) {
        TBGame game = new TBGame(features: [allX]
        )

        initializer.initializeGame(game)
        assert [shipX, shipX, shipX, shipX, shipX] == game.startingShips
    }
}
