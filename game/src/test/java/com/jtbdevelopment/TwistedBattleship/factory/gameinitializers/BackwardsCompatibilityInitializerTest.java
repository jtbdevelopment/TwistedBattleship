package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Date: 6/10/16
 * Time: 7:08 PM
 */
public class BackwardsCompatibilityInitializerTest {
    private BackwardsCompatibilityInitializer initializer = new BackwardsCompatibilityInitializer();

    @Test
    public void testInitializeGameMissingOptions() {
        TBGame game = new TBGame();
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.StandardShips)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingCruiseMissile1() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.CruiseMissileDisabled)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.StandardShips)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingCruiseMissile2() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.CruiseMissileEnabled)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileEnabled, GameFeature.StandardShips)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips1() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.StandardShips)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.StandardShips)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips2() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.AllCarriers)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.AllCarriers)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips3() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.AllSubmarines)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.AllSubmarines)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips4() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.AllDestroyers)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.AllDestroyers)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips5() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.AllBattleships)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.AllBattleships)),
                game.getFeatures());
    }

    @Test
    public void testInitializeGameMNotMissingStartingShips6() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Collections.singletonList(GameFeature.AllCruisers)));
        initializer.initializeGame(game);
        assertEquals(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileDisabled, GameFeature.AllCruisers)),
                game.getFeatures());
    }

    @Test
    public void testGetOrder() {
        assertEquals(GameInitializer.EARLY_ORDER, initializer.getOrder());
    }
}
