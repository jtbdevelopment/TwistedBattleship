package com.jtbdevelopment.TwistedBattleship.push;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.PlayerState;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;

/**
 * Date: 10/15/15
 * Time: 6:45 PM
 */
public class TBPushWorthyFilterTest extends MongoGameCoreTestCase {
    private TBPushWorthyFilter filter = new TBPushWorthyFilter();

    @Test
    public void testDeclined() {
        TBGame game = new TBGame();

        game.setGamePhase(GamePhase.Declined);

        Assert.assertFalse(filter.shouldPush(null, game));
    }

    @Test
    public void testQuitAlwaysFalse() {
        TBGame game = new TBGame();

        game.setGamePhase(GamePhase.Quit);

        Assert.assertFalse(filter.shouldPush(null, game));
    }

    @Test
    public void testNextRoundStartedAlwaysFalse() {
        TBGame game = new TBGame();

        game.setGamePhase(GamePhase.NextRoundStarted);

        Assert.assertFalse(filter.shouldPush(null, game));
    }

    @Test
    public void testRoundOverAlwaysTrue() {
        TBGame game = new TBGame();

        game.setGamePhase(GamePhase.RoundOver);

        assert filter.shouldPush(null, game);
    }

    @Test
    public void testPlayingNotCurrentPlayer() {
        TBGame game = new TBGame();


        game.setGamePhase(GamePhase.Playing);
        game.setCurrentPlayer(MongoGameCoreTestCase.PONE.getId());

        Assert.assertFalse(filter.shouldPush(MongoGameCoreTestCase.PTHREE, game));
    }

    @Test
    public void testPlayingAndIsCurrentPlayer() {
        TBGame game = new TBGame();

        game.setGamePhase(GamePhase.Playing);
        game.setCurrentPlayer(MongoGameCoreTestCase.PONE.getId());

        assertTrue(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
    }

    @Test
    public void testChallengedAndThisPlayerPendingTrue() {
        TBGame game = new TBGame();

        LinkedHashMap<ObjectId, PlayerState> map = new LinkedHashMap<>(1);
        map.put(PONE.getId(), PlayerState.Pending);
        game.setGamePhase(GamePhase.Challenged);
        game.setPlayerStates(map);

        assertTrue(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
    }

    @Test
    public void testChallengedAndThisPlayerNotPendingIsFalse() {
        TBGame game = new TBGame();

        LinkedHashMap<ObjectId, PlayerState> map = new LinkedHashMap<>(1);
        map.put(PONE.getId(), PlayerState.Accepted);

        game.setGamePhase(GamePhase.Challenged);
        game.setPlayerStates(map);
        Assert.assertFalse(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
        game.getPlayerStates().put(PONE.getId(), PlayerState.Rejected);
        Assert.assertFalse(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
    }

    @Test
    public void testSetupAndCurrentPlayerNotSetupIsTrue() {
        TBGame game = new TBGame();

        Map<ObjectId, TBPlayerState> map = new LinkedHashMap<>(1);
        map.put(PONE.getId(), Mockito.mock(TBPlayerState.class));
        Mockito.when(map.get(PONE.getId()).isSetup()).thenReturn(false);
        game.setGamePhase(GamePhase.Setup);
        game.setPlayerDetails(map);
        assertTrue(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
    }

    @Test
    public void testSetupAndCurrentPlayerIsSetupIsFalse() {
        TBGame game = new TBGame();

        LinkedHashMap<ObjectId, TBPlayerState> map = new LinkedHashMap<>(1);
        map.put(PONE.getId(), Mockito.mock(TBPlayerState.class));
        Mockito.when(map.get(PONE.getId()).isSetup()).thenReturn(true);

        game.setGamePhase(GamePhase.Setup);
        game.setPlayerDetails(map);
        Assert.assertFalse(filter.shouldPush(MongoGameCoreTestCase.PONE, game));
    }
}
