package com.jtbdevelopment.TwistedBattleship.factory.gameinitializers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameInitializer;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/6/15
 * Time: 6:34 PM
 */
public class MovesInitializerTest extends MongoGameCoreTestCase {
    private MovesInitializer initializer = new MovesInitializer();

    @Test
    public void testGetOrder() {
        assertEquals(GameInitializer.DEFAULT_ORDER, initializer.getOrder());
    }

    @Test
    public void testInitializeGame() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.Single, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));

        initializer.initializeGame(game);

        assertEquals(1, game.getRemainingMoves());
        assertEquals(1, game.getMovesForSpecials());
    }

    @Test
    public void testInitializePerShipGame() {
        TBGame game = new TBGame();
        game.setFeatures(new HashSet<>(Arrays.asList(GameFeature.Grid15x15, GameFeature.PerShip, GameFeature.ECMEnabled, GameFeature.EREnabled, GameFeature.SpyEnabled, GameFeature.EMEnabled, GameFeature.CruiseMissileEnabled)));
        game.setPlayers(Arrays.asList(PONE, PTWO, PTHREE));

        initializer.initializeGame(game);

        assertEquals(5, game.getRemainingMoves());
        assertEquals(2, game.getMovesForSpecials());
    }
}
