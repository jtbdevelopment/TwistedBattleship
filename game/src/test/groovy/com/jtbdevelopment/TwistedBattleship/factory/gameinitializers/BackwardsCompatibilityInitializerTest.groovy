package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameInitializer

/**
 * Date: 6/10/16
 * Time: 7:08 PM
 */
class BackwardsCompatibilityInitializerTest extends GroovyTestCase {
    BackwardsCompatibilityInitializer initializer = new BackwardsCompatibilityInitializer()

    void testInitializeGameMissingOptions() {
        TBGame game = new TBGame()
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled] as Set == game.features
    }

    void testInitializeGameMNotMissingCruiseMissile1() {
        TBGame game = new TBGame(features: [GameFeature.CruiseMissileDisabled] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled] as Set == game.features
    }

    void testInitializeGameMNotMissingCruiseMissile2() {
        TBGame game = new TBGame(features: [GameFeature.CruiseMissileEnabled] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileEnabled] as Set == game.features
    }

    void testGetOrder() {
        assert GameInitializer.EARLY_ORDER == initializer.order
    }
}
