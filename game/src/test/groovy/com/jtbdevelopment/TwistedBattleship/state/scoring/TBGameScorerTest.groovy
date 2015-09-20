package com.jtbdevelopment.TwistedBattleship.state.scoring

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase

/**
 * Date: 4/27/15
 * Time: 6:25 PM
 */
class TBGameScorerTest extends MongoGameCoreTestCase {
    TBGame game = new TBGame(
            playerDetails: [
                    (PONE.id): new TBPlayerState(shipStates: [new ShipState(Ship.Battleship, [] as SortedSet<GridCoordinate>)], scoreFromLiving: 5),
                    (PTWO.id): new TBPlayerState(scoreFromLiving: 2),
            ]
    )

    TBGameScorer scorer = new TBGameScorer()

    void testScoresLiving() {
        assert game.is(scorer.scoreGame(game))
        assert 15 == game.playerDetails[PONE.id].scoreFromLiving
        assert 2 == game.playerDetails[PTWO.id].scoreFromLiving
    }
}
