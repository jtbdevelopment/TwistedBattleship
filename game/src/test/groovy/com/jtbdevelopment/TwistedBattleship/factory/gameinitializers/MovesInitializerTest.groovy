package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class MovesInitializerTest extends MongoGameCoreTestCase {
    MovesInitializer initializer = new MovesInitializer()

    void testGetOrder() {
        assert GameInitializer.DEFAULT_ORDER == initializer.order
    }

    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)

        assert 1 == game.remainingMoves
        assert 1 == game.movesForSpecials
    }

    void testInitializePerShipGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.PerShip, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)

        assert 5 == game.remainingMoves
        assert 2 == game.movesForSpecials
    }
}
