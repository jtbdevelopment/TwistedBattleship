package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import org.bson.types.ObjectId

/**
 * Date: 5/20/15
 * Time: 6:38 PM
 */
class ECMHandlerTest extends AbstractBaseHandlerTest {
    ECMHandler handler = new ECMHandler()

    void testTargetSelf() {
        assert handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesECMsRemain() {
        game.playerDetails[PONE.id].ecmsRemaining = 1
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        game.playerDetails[PONE.id].ecmsRemaining = 0
        shouldFail(NoECMActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        })
    }

    //  No need to test isolated vs shared intel on this mov

    void testAnECM() {
        handler.gridCircleUtil = new GridCircleUtil()
        handler.gridCircleUtil.gridSizeUtil = new GridSizeUtil()
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 5, GridCellState.KnownByMiss)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 6, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 7, GridCellState.KnownByRehit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(5, 8, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 1, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 5, GridCellState.KnownByMiss)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 6, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 7, GridCellState.KnownByRehit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(5, 8, GridCellState.KnownByOtherHit)

        game.playerDetails[PFOUR.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PFOUR.id].set(2, 0, GridCellState.KnownByHit)

        assert game.is(handler.playMove(PONE, game, PONE, new GridCoordinate(1, 0)))
        assert GridCellState.KnownByMiss == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 5)
        assert GridCellState.KnownByHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 6)
        assert GridCellState.KnownByRehit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 7)
        assert GridCellState.KnownByOtherHit == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(5, 8)
        assert GridCellState.KnownByMiss == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 5)
        assert GridCellState.KnownByHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 6)
        assert GridCellState.KnownByRehit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 7)
        assert GridCellState.KnownByOtherHit == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(5, 8)
        assert 2 == game.playerDetails[PONE.id].ecmsRemaining
        assert game.playerDetails[PONE.id].lastActionMessage == "1 deployed an ECM at (1,0)."

        game.playerDetails.findAll { it.key != PONE.id }.each {
            ObjectId id, TBPlayerState state ->
                assert state.lastActionMessage == "1 deployed an ECM at (1,0)."
                (0..14).each {
                    int row ->
                        (0..14).each {
                            int col ->
                                if (!(id == PTWO.id && row == 5 && col >= 5 && col <= 8)) {
                                    assert GridCellState.Unknown == state.opponentGrids[PONE.id].get(row, col)
                                    assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[id].get(row, col)
                                }
                        }
                }
        }
    }
}
