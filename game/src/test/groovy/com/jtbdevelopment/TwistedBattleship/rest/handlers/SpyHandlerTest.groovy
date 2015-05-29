package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

/**
 * Date: 5/15/15
 * Time: 6:56 AM
 */
class SpyHandlerTest extends AbstractBaseHandlerTest {
    SpyHandler handler = new SpyHandler()

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        handler.gridCircleUtil = new GridCircleUtil()
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 1, GridCellState.KnownByMiss)

        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(2, 0, GridCellState.KnownByHit)

        game.playerDetails[PONE.id].shipStates[Ship.Carrier].healthRemaining = 3
        game.playerDetails[PONE.id].shipStates[Ship.Carrier].shipSegmentHit = [false, true, true, false, false]
    }

    void testTargetSelf() {
        assertFalse handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesNoSpiesRemain() {
        TBGame game = new TBGame(
                playerDetails: [
                        (PONE.id): new TBPlayerState(spysRemaining: 1)
                ]
        )
        handler.validateMoveSpecific(PONE, game, PTWO, null)
        game.playerDetails[PONE.id].spysRemaining = 0
        shouldFail(NoSpyActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PTWO, null)
        })
    }

    void testSpyWithIsolatedIntel() {
        game.features.add(GameFeature.IsolatedIntel)

        TBGame g = handler.playMove(PTWO, game, PONE, new GridCoordinate(2, 1))

        assert game.is(g)
        coreSpyAsserts()
        (0..14).each {
            int row ->
                int start = row < 5 ? 4 : 0
                (0..14).each {
                    int col ->
                        if (start <= col) {
                            assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(row, col)
                        }
                        if (row == 2 && col == 0) {
                            assert GridCellState.KnownByHit == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                        } else {
                            assert GridCellState.Unknown == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                        }
                        assert GridCellState.Unknown == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(row, col)
                        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(row, col)
                }
        }
        assert "" == game.playerDetails[PTHREE.id].lastActionMessage
        assert "" == game.playerDetails[PFOUR.id].lastActionMessage
    }

    void testSpyWithSharedIntel() {
        game.features.add(GameFeature.SharedIntel)

        TBGame g = handler.playMove(PTWO, game, PONE, new GridCoordinate(2, 1))

        assert game.is(g)
        coreSpyAsserts()
        (0..14).each {
            int row ->
                int start = row < 5 ? 4 : 0
                (0..14).each {
                    int col ->
                        if (start <= col) {
                            assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(row, col)
                        }
                        if (row == 1 && col == 0) {
                            assert GridCellState.KnownByOtherHit == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.KnownByOtherHit == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                            assert GridCellState.KnownByOtherHit == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.KnownByOtherHit == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(row, col)
                        } else if (row == 1 && col == 1) {
                            assert GridCellState.KnownEmpty == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                            assert GridCellState.KnownEmpty == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(row, col)
                            assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(row, col)
                        } else {
                            assert game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(row, col) == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(row, col)
                            assert game.playerDetails[PONE.id].opponentViews[PTWO.id].get(row, col) == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(row, col)
                            if (row == 2 && col == 0) {
                                assert GridCellState.KnownByHit == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                                assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                            } else {
                                assert game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(row, col) == game.playerDetails[PFOUR.id].opponentGrids[PONE.id].get(row, col)
                                assert game.playerDetails[PONE.id].opponentViews[PTWO.id].get(row, col) == game.playerDetails[PONE.id].opponentViews[PFOUR.id].get(row, col)
                            }
                        }
                }
        }
        assert "2 spied on 1 at (2,1)." == game.playerDetails[PTHREE.id].lastActionMessage
        assert "2 spied on 1 at (2,1)." == game.playerDetails[PFOUR.id].lastActionMessage
    }

    protected void coreSpyAsserts() {
        assert 3 == game.playerDetails[PTHREE.id].spysRemaining
        assert 3 == game.playerDetails[PFOUR.id].spysRemaining
        assert 3 == game.playerDetails[PONE.id].spysRemaining
        assert 2 == game.playerDetails[PTWO.id].spysRemaining
        assert "2 spied on 1 at (2,1)." == game.playerDetails[PONE.id].lastActionMessage
        assert "2 spied on 1 at (2,1)." == game.playerDetails[PTWO.id].lastActionMessage
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(4, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(4, 1)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(4, 2)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(4, 3)
        assert GridCellState.KnownShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 2)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 3)
        assert GridCellState.KnownByOtherHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 2)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 3)
        assert GridCellState.KnownByHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 0)
        assert GridCellState.KnownByMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 2)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 3)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 1)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 2)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 3)

        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(4, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(4, 1)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(4, 2)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(4, 3)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 2)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 3)
        assert GridCellState.KnownByOtherHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 2)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 3)
        assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 0)
        assert GridCellState.KnownByMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 1)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 2)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 3)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 0)
        assert GridCellState.KnownEmpty == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 1)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 2)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 3)
    }

}
