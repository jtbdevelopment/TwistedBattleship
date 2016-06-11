package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class GridSizeInitializerTest extends MongoGameCoreTestCase {
    GridSizeInitializer initializer = new GridSizeInitializer()

    void testGetOrder() {
        assert GameInitializer.DEFAULT_ORDER == initializer.order
    }

    void testInitializeGameGrid10x10() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid10x10, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)
        assert 10 == game.gridSize
    }

    void testInitializeGameGrid15x15() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)
        assert 15 == game.gridSize
    }


    void testInitializeGameGrid20x20() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid20x20, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)
        assert 20 == game.gridSize
    }
}
