package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
class CurrentPlayerInitializerTest extends MongoGameCoreTestCase {
    CurrentPlayerInitializer initializer = new CurrentPlayerInitializer()

    void testGetOrder() {
        assert GameInitializer.DEFAULT_ORDER == initializer.order
    }

    void testInitializeGame() {
        TBGame game = new TBGame(
                features: [GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled],
                players: [PONE, PTWO, PTHREE]
        )

        initializer.initializeGame(game)
        assert PONE.id == game.currentPlayer
    }

}
