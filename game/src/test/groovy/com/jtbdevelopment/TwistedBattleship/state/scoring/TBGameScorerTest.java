package com.jtbdevelopment.TwistedBattleship.state.scoring;

import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.mongo.players.MongoSystemPlayer;
import com.jtbdevelopment.games.publish.PlayerPublisher;
import org.bson.types.ObjectId;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
public class TBGameScorerTest extends MongoGameCoreTestCase {
    private MongoPlayer loaded1 = makeSimplePlayer(PONE.getIdAsString());
    private MongoPlayer loaded2 = makeSimplePlayer(PTWO.getIdAsString());
    private MongoPlayer saved1 = new MongoPlayer();
    private MongoPlayer saved2 = new MongoPlayer();
    private AbstractPlayerRepository<ObjectId, MongoPlayer> mockRepository = Mockito.mock(AbstractPlayerRepository.class);
    private PlayerPublisher mockPublisher = Mockito.mock(PlayerPublisher.class);
    private MongoSystemPlayer systemPlayer = new MongoSystemPlayer();
    private TBGame game;
    private TBGameScorer scorer;

    @Before
    public void setUp() {
        game = new TBGame();

        Map<ObjectId, TBPlayerState> map = new HashMap<>();
        TBPlayerState state = new TBPlayerState();
        state.setShipStates(Collections.singletonList(new ShipState(Ship.Battleship, new TreeSet<>())));
        state.setScoreFromLiving(5);

        map.put(PONE.getId(), state);
        state = new TBPlayerState();
        state.setScoreFromLiving(2);
        map.put(PTWO.getId(), state);

        game.setPlayers(Arrays.asList(PONE, PTWO, systemPlayer));
        game.setPlayerDetails(map);
        Mockito.when(mockRepository.findById(PONE.getId())).thenReturn(Optional.of(loaded1));
        Mockito.when(mockRepository.findById(PTWO.getId())).thenReturn(Optional.of(loaded2));
        Mockito.when(mockRepository.save(PONE)).thenReturn(saved1);
        Mockito.when(mockRepository.save(PTWO)).thenReturn(saved2);
        loaded1.setGameSpecificPlayerAttributes(new TBPlayerAttributes());
        loaded2.setGameSpecificPlayerAttributes(new TBPlayerAttributes());
        scorer = new TBGameScorer();
        scorer.setPlayerPublisher(mockPublisher);
        scorer.setPlayerRepository(mockRepository);
    }

    @Test
    public void testScoresLiving() {
        Assert.assertSame(game, scorer.scoreGame(game));
        assertEquals(15, game.getPlayerDetails().get(PONE.getId()).getScoreFromLiving());
        assertEquals(2, game.getPlayerDetails().get(PTWO.getId()).getScoreFromLiving());
        assertEquals(15, game.getPlayerDetails().get(PONE.getId()).getTotalScore());
        assertEquals(2, game.getPlayerDetails().get(PTWO.getId()).getTotalScore());
    }

    @Test
    public void testAdjustsPlayerWinStreaksAndWinsLosses() {
        ((TBPlayerAttributes) loaded1.getGameSpecificPlayerAttributes()).setCurrentWinStreak(5);
        ((TBPlayerAttributes) loaded1.getGameSpecificPlayerAttributes()).setWins(10);
        ((TBPlayerAttributes) loaded1.getGameSpecificPlayerAttributes()).setLosses(10);
        ((TBPlayerAttributes) loaded1.getGameSpecificPlayerAttributes()).setHighestScore(11);
        ((TBPlayerAttributes) loaded2.getGameSpecificPlayerAttributes()).setCurrentWinStreak(5);
        ((TBPlayerAttributes) loaded2.getGameSpecificPlayerAttributes()).setWins(10);
        ((TBPlayerAttributes) loaded2.getGameSpecificPlayerAttributes()).setLosses(11);
        ((TBPlayerAttributes) loaded2.getGameSpecificPlayerAttributes()).setHighestScore(5);

        scorer.scoreGame(game);

        Mockito.verify(mockPublisher).publish(saved2);
        Mockito.verify(mockPublisher).publish(saved1);
        TBPlayerAttributes attributes = loaded1.getGameSpecificPlayerAttributes();
        assertEquals(6, attributes.getCurrentWinStreak());
        assertEquals(11, attributes.getWins());
        assertEquals(10, attributes.getLosses());
        assertEquals(15, attributes.getHighestScore());
        attributes = loaded2.getGameSpecificPlayerAttributes();
        assertEquals(0, attributes.getCurrentWinStreak());
        assertEquals(10, attributes.getWins());
        assertEquals(12, attributes.getLosses());
        assertEquals(5, attributes.getHighestScore());
    }
}
