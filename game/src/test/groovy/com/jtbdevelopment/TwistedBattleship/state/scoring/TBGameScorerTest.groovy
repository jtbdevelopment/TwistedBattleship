package com.jtbdevelopment.TwistedBattleship.state.scoring

import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.mongo.players.MongoSystemPlayer
import com.jtbdevelopment.games.publish.PlayerPublisher
import org.bson.types.ObjectId
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
class TBGameScorerTest extends MongoGameCoreTestCase {
    def loaded1 = (MongoPlayer) PONE.clone()
    def loaded2 = (MongoPlayer) PTWO.clone()
    def saved1 = new MongoPlayer()
    def saved2 = new MongoPlayer()
    private AbstractPlayerRepository<ObjectId, MongoPlayer> mockRepository = Mockito.mock(AbstractPlayerRepository.class)
    private PlayerPublisher mockPublisher = Mockito.mock(PlayerPublisher.class)

    MongoSystemPlayer systemPlayer = new MongoSystemPlayer()

    TBGame game = new TBGame(
            players: [PONE, PTWO, systemPlayer],  //  3 has no state or attributes for error handling
            playerDetails: [
                    (PONE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)], scoreFromLiving: 5),
                    (PTWO.id): new TBPlayerState(scoreFromLiving: 2),
            ]
    )

    TBGameScorer scorer = new TBGameScorer(playerRepository: mockRepository, playerPublisher: mockPublisher)

    @Before
    void setUp() {
        Mockito.when(mockRepository.findById(PONE.id)).thenReturn(Optional.of(loaded1))
        Mockito.when(mockRepository.findById(PTWO.id)).thenReturn(Optional.of(loaded2))
        Mockito.when(mockRepository.save(PONE)).thenReturn(saved1)
        Mockito.when(mockRepository.save(PTWO)).thenReturn(saved2)
        loaded1.gameSpecificPlayerAttributes = new TBPlayerAttributes()
        loaded2.gameSpecificPlayerAttributes = new TBPlayerAttributes()
    }

    @Test
    void testScoresLiving() {
        assert game.is(scorer.scoreGame(game))
        assert 15 == game.playerDetails[PONE.id].scoreFromLiving
        assert 2 == game.playerDetails[PTWO.id].scoreFromLiving
        assert 15 == game.playerDetails[PONE.id].totalScore
        assert 2 == game.playerDetails[PTWO.id].totalScore
    }

    @Test

    void testAdjustsPlayerWinStreaksAndWinsLosses() {
        ((TBPlayerAttributes) loaded1.gameSpecificPlayerAttributes).currentWinStreak = 5
        ((TBPlayerAttributes) loaded1.gameSpecificPlayerAttributes).wins = 10
        ((TBPlayerAttributes) loaded1.gameSpecificPlayerAttributes).losses = 10
        ((TBPlayerAttributes) loaded1.gameSpecificPlayerAttributes).highestScore = 11
        ((TBPlayerAttributes) loaded2.gameSpecificPlayerAttributes).currentWinStreak = 5
        ((TBPlayerAttributes) loaded2.gameSpecificPlayerAttributes).wins = 10
        ((TBPlayerAttributes) loaded2.gameSpecificPlayerAttributes).losses = 11
        ((TBPlayerAttributes) loaded2.gameSpecificPlayerAttributes).highestScore = 5

        scorer.scoreGame(game)

        Mockito.verify(mockPublisher).publish(saved2)
        Mockito.verify(mockPublisher).publish(saved1)
        TBPlayerAttributes attributes = (TBPlayerAttributes) loaded1.gameSpecificPlayerAttributes
        assert 6 == attributes.currentWinStreak
        assert 11 == attributes.wins
        assert 10 == attributes.losses
        assert 15 == attributes.highestScore
        attributes = (TBPlayerAttributes) loaded2.gameSpecificPlayerAttributes
        assert 0 == attributes.currentWinStreak
        assert 10 == attributes.wins
        assert 12 == attributes.losses
        assert 5 == attributes.highestScore
    }

}
