package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class StartingShipsInitializerTest extends MongoGameCoreTestCase {
    StartingShipsInitializer initializer = new StartingShipsInitializer()

    void testGetOrder() {
        assert GameInitializer.DEFAULT_ORDER == initializer.order
    }

    void testInitializeGame() {
        TBGame game = new TBGame(
        )

        initializer.initializeGame(game)
        assert Ship.values().toList() == game.startingShips
    }
}
