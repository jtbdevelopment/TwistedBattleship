package com.jtbdevelopment.TwistedBattleship.factory;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import org.bson.types.ObjectId;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Date: 4/5/2015
 * Time: 2:42 PM
 */
public class TBGameFactoryTest extends MongoGameCoreTestCase {
    private TBGameFactory gameFactory = new TBGameFactory(Collections.emptyList(), Collections.emptyList());

    @Test
    public void testNewGame() {
        assertTrue(gameFactory.newGame() instanceof TBGame);
    }

    @Test
    public void testCopyFromPreviousGame() {
        TBGame previousGame = new TBGame();
        previousGame.setId(new ObjectId());
        previousGame.setFeatures(new HashSet<>(Arrays.asList(GameFeature.CruiseMissileEnabled, GameFeature.ECMDisabled)));

        TBGame newGame = new TBGame();
        newGame.setFeatures(new HashSet<>(previousGame.getFeatures()));

        gameFactory.copyFromPreviousGame(previousGame, newGame);

        assertEquals(newGame.getPreviousId(), previousGame.getId());
        assertEquals(newGame.getFeatures(), previousGame.getFeatures());
    }

    @Test
    public void testCopyFromPreviousGameAddsMissingDefaults() {
        TBGame previousGame = new TBGame();
        previousGame.setId(new ObjectId());
        previousGame.setFeatures(new HashSet<>(Arrays.asList(GameFeature.SpyDisabled, GameFeature.ECMDisabled)));
        TBGame newGame = new TBGame();
        newGame.setFeatures(new HashSet<>(previousGame.getFeatures()));

        gameFactory.copyFromPreviousGame(previousGame, newGame);

        assertEquals(newGame.getPreviousId(), previousGame.getId());
        assertTrue(newGame.getFeatures().containsAll(previousGame.getFeatures()));
        assertTrue(newGame.getFeatures().contains(GameFeature.CruiseMissileDisabled));
        assertEquals(newGame.getFeatures().size(), (previousGame.getFeatures().size() + 1));
    }
}
