package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.junit.Assert
import org.junit.Test

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class MovesInitializerTest extends MongoGameCoreTestCase {
    private MovesInitializer initializer = new MovesInitializer()

    @Test
    void testGetOrder() {
        Assert.assertEquals GameInitializer.DEFAULT_ORDER, initializer.order
    }

    @Test
    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)

        Assert.assertEquals 1, game.remainingMoves
        Assert.assertEquals 1, game.movesForSpecials
    }

    @Test
    void testInitializePerShipGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.PerShip, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)

        Assert.assertEquals 5, game.remainingMoves
        Assert.assertEquals 2, game.movesForSpecials
    }
}
