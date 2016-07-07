package com.jtbdevelopment.TwistedBattleship.state.scoring

import com.jtbdevelopment.TwistedBattleship.player.TBPlayerAttributes
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.publish.PlayerPublisher

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
class TBGameScorerTest extends MongoGameCoreTestCase {
    def savedPlayers = [:]
    def publishedPlayers = []
    AbstractPlayerRepository mockRepository = [
            save: {
                Player p ->
                    savedPlayers[p.id] = p
                    p
            }
    ] as AbstractPlayerRepository
    PlayerPublisher mockPublisher = [
            publish: {
                Player p ->
                    publishedPlayers.add(p)
            }
    ] as PlayerPublisher
    TBGame game = new TBGame(
            players: [PONE, PTWO],
            playerDetails: [
                    (PONE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)], scoreFromLiving: 5),
                    (PTWO.id): new TBPlayerState(scoreFromLiving: 2),
            ]
    )

    TBGameScorer scorer = new TBGameScorer(playerRepository: mockRepository, playerPublisher: mockPublisher)

    void setup() {
        savedPlayers.clear()
        publishedPlayers.clear()
    }

    void testScoresLiving() {
        assert game.is(scorer.scoreGame(game))
        assert 15 == game.playerDetails[PONE.id].scoreFromLiving
        assert 2 == game.playerDetails[PTWO.id].scoreFromLiving
        assert 15 == game.playerDetails[PONE.id].totalScore
        assert 2 == game.playerDetails[PTWO.id].totalScore
    }

    void testAdjustsPlayerWinStreaksAndWinsLosses() {
        PONE.gameSpecificPlayerAttributes = new TBPlayerAttributes()
        ((TBPlayerAttributes) PONE.gameSpecificPlayerAttributes).currentWinStreak = 5
        ((TBPlayerAttributes) PONE.gameSpecificPlayerAttributes).wins = 10
        ((TBPlayerAttributes) PONE.gameSpecificPlayerAttributes).losses = 10
        ((TBPlayerAttributes) PONE.gameSpecificPlayerAttributes).highestScore = 11
        PTWO.gameSpecificPlayerAttributes = new TBPlayerAttributes()
        ((TBPlayerAttributes) PTWO.gameSpecificPlayerAttributes).currentWinStreak = 5
        ((TBPlayerAttributes) PTWO.gameSpecificPlayerAttributes).wins = 10
        ((TBPlayerAttributes) PTWO.gameSpecificPlayerAttributes).losses = 11
        ((TBPlayerAttributes) PTWO.gameSpecificPlayerAttributes).highestScore = 5

        scorer.scoreGame(game)

        assert [(PONE.id): PONE, (PTWO.id): PTWO] == savedPlayers
        assert [PONE, PTWO] as Set == publishedPlayers as Set
        TBPlayerAttributes attributes = (TBPlayerAttributes) ((Player) savedPlayers[PONE.id]).gameSpecificPlayerAttributes
        assert 6 == attributes.currentWinStreak
        assert 11 == attributes.wins
        assert 10 == attributes.losses
        assert 15 == attributes.highestScore
        attributes = (TBPlayerAttributes) ((Player) savedPlayers[PTWO.id]).gameSpecificPlayerAttributes
        assert 0 == attributes.currentWinStreak
        assert 10 == attributes.wins
        assert 12 == attributes.losses
        assert 5 == attributes.highestScore
    }

}
