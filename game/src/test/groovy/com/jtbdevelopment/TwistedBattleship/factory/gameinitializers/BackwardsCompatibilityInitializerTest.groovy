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
        assert [GameFeature.CruiseMissileDisabled, GameFeature.StandardShips] as Set == game.features
    }

    void testInitializeGameMNotMissingCruiseMissile1() {
        TBGame game = new TBGame(features: [GameFeature.CruiseMissileDisabled] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.StandardShips] as Set == game.features
    }

    void testInitializeGameMNotMissingCruiseMissile2() {
        TBGame game = new TBGame(features: [GameFeature.CruiseMissileEnabled] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileEnabled, GameFeature.StandardShips] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips1() {
        TBGame game = new TBGame(features: [GameFeature.StandardShips] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.StandardShips] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips2() {
        TBGame game = new TBGame(features: [GameFeature.AllCarriers] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.AllCarriers] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips3() {
        TBGame game = new TBGame(features: [GameFeature.AllSubmarines] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.AllSubmarines] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips4() {
        TBGame game = new TBGame(features: [GameFeature.AllDestroyers] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.AllDestroyers] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips5() {
        TBGame game = new TBGame(features: [GameFeature.AllBattleships] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.AllBattleships] as Set == game.features
    }

    void testInitializeGameMNotMissingStartingShips6() {
        TBGame game = new TBGame(features: [GameFeature.AllCruisers] as Set)
        initializer.initializeGame(game)
        assert [GameFeature.CruiseMissileDisabled, GameFeature.AllCruisers] as Set == game.features
    }

    void testGetOrder() {
        assert GameInitializer.EARLY_ORDER == initializer.order
    }
}
