package com.jtbdevelopment.TwistedBattleship.state.grid

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.mongo.MongoGameCoreTestCase
import org.junit.Test

/**
 * Date: 9/29/2015
 * Time: 10:07 PM
 */
class ConsolidateGridViewsTest extends MongoGameCoreTestCase {
    ConsolidateGridViews consolidateGridViews = new ConsolidateGridViews()

    @Test
    public void testConsolidate() {
        TBGame game = new TBGame(
                gridSize: 10,
                playerDetails: [
                        (PONE.id)  : new TBPlayerState(
                                opponentViews: [
                                        (PTWO.id)  : new Grid(10),
                                        (PTHREE.id): new Grid(10)
                                ]),
                        (PTWO.id)  : new TBPlayerState(),
                        (PTHREE.id): new TBPlayerState(),
                ]
        )
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 1, GridCellState.KnownByMiss)

        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 2, GridCellState.Unknown)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 2, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 3, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 3, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 4, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(5, 4, GridCellState.KnownByRehit)

        Grid consolidatedOpponentView = consolidateGridViews.createConsolidatedView(game, game.playerDetails[PONE.id].opponentViews.values())
        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {
                def state = consolidatedOpponentView.get(row, col)
                if (row != 5 || col > 4) {
                    assert state == GridCellState.Unknown
                } else {
                    switch (col) {
                        case 0:
                            assert GridCellState.KnownByHit == state
                            break
                        case 1:
                            assert GridCellState.KnownByMiss == state;
                            break
                        case 2:
                            assert GridCellState.KnownShip == state;
                            break
                        case 3:
                            assert GridCellState.KnownByHit == state;
                            break
                        case 4:
                            assert GridCellState.KnownByHit == state
                            break
                    }
                }
            }
        }

    }
}
