package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
public class GridSizeInitializerTest extends MongoGameCoreTestCase {
    private GridSizeInitializer initializer = new GridSizeInitializer();

    @Test
    public void testGetOrder() {
        Assert.assertEquals(GameInitializer.DEFAULT_ORDER, initializer.getOrder());
    }

    @Test
    public void testInitializeGameGrid10x10() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid10x10, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));

        initializer.initializeGame(game);
        Assert.assertEquals(10, game.getGridSize());
    }

    @Test
    public void testInitializeGameGrid15x15() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));

        initializer.initializeGame(game);
        Assert.assertEquals(15, game.getGridSize());
    }

    @Test
    public void testInitializeGameGrid20x20() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid20x20, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));

        initializer.initializeGame(game);
        Assert.assertEquals(20, game.getGridSize());
    }
}
