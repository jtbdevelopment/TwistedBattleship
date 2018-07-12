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
public class CurrentPlayerInitializerTest extends MongoGameCoreTestCase {
    private CurrentPlayerInitializer initializer = new CurrentPlayerInitializer();

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
        assertEquals(PONE.getId(), game.getCurrentPlayer());
    }
}
