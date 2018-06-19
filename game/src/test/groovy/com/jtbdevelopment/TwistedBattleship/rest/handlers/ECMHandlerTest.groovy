package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import org.bson.types.ObjectId
import org.junit.Test

/**
 * Date: 5/20/15
 * Time: 6:38 PM
 */
class ECMHandlerTest extends AbstractBaseHandlerTest {
    ECMHandler handler = new ECMHandler()

    @Test
    void testTargetSelf() {
        assert handler.targetSelf()
    }

    @Test
    void testMovesRequired() {
        TBGame game = new TBGame(movesForSpecials: 1)
        assert 1 == handler.movesRequired(game)
        game = new TBGame(movesForSpecials: 2)
        assert 2 == handler.movesRequired(game)
    }

    @Test
    void testValidatesECMsRemain() {
        game.playerDetails[PONE.id].ecmsRemaining = 1
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        game.playerDetails[PONE.id].ecmsRemaining = 0
        shouldFail(NoECMActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        })
    }

    //  No need to test isolated vs shared intel on this mov

    @Test
    void testAnECM() {
        handler.gridCircleUtil = new GridCircleUtil()
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

        game.playerDetails[PONE.id].actionLog.add(new TBActionLogEntry(
                description: "You fired at " + PTWO.displayName + " " + new GridCoordinate(1, 0) + " something",
                actionType: TBActionLogEntry.TBActionType.Fired
        ))
        game.playerDetails[PTWO.id].actionLog.add(new TBActionLogEntry(
                description: "You fired at " + PONE.displayName + " " + new GridCoordinate(1, 0) + " something",
                actionType: TBActionLogEntry.TBActionType.Fired
        ))
        game.playerDetails[PTWO.id].actionLog.add(new TBActionLogEntry(
                description: "You fired at " + PONE.displayName + " " + new GridCoordinate(0, 0) + " something",
                actionType: TBActionLogEntry.TBActionType.Fired
        ))
        game.playerDetails[PTWO.id].actionLog.add(new TBActionLogEntry(
                description: "You fired at " + PONE.displayName + " " + new GridCoordinate(0, 0) + " something",
                actionType: TBActionLogEntry.TBActionType.Spied     //  stupid type on purpose
        ))
        game.playerDetails[PTWO.id].actionLog.add(new TBActionLogEntry(
                description: "You X at " + PONE.displayName + " " + new GridCoordinate(0, 0) + " something",  // silly desc on purpose
                actionType: TBActionLogEntry.TBActionType.Fired
        ))
        game.playerDetails[PTWO.id].actionLog.add(new TBActionLogEntry(
                description: "You fired at " + PONE.displayName + " " + new GridCoordinate(10, 10) + " something",
                actionType: TBActionLogEntry.TBActionType.Fired
        ))

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
        assert game.playerDetails[PONE.id].actionLog[-1].description == "1 deployed an ECM."
        assert game.playerDetails[PONE.id].actionLog[-1].actionType == TBActionLogEntry.TBActionType.UsedECM

        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PONE.id].actionLog[0].actionType
        assert "You fired at " + PTWO.displayName + " " + new GridCoordinate(1, 0) + " something" == game.playerDetails[PONE.id].actionLog[0].description
        assert TBActionLogEntry.TBActionType.DamagedByECM == game.playerDetails[PTWO.id].actionLog[0].actionType
        assert "Log damaged by ECM." == game.playerDetails[PTWO.id].actionLog[0].description
        assert TBActionLogEntry.TBActionType.DamagedByECM == game.playerDetails[PTWO.id].actionLog[1].actionType
        assert "Log damaged by ECM." == game.playerDetails[PTWO.id].actionLog[1].description
        assert TBActionLogEntry.TBActionType.Spied == game.playerDetails[PTWO.id].actionLog[2].actionType
        assert "You fired at " + PONE.displayName + " " + new GridCoordinate(0, 0) + " something" == game.playerDetails[PTWO.id].actionLog[2].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[3].actionType
        assert "You X at " + PONE.displayName + " " + new GridCoordinate(0, 0) + " something" == game.playerDetails[PTWO.id].actionLog[3].description
        assert TBActionLogEntry.TBActionType.Fired == game.playerDetails[PTWO.id].actionLog[4].actionType
        assert "You fired at " + PONE.displayName + " " + new GridCoordinate(10, 10) + " something" == game.playerDetails[PTWO.id].actionLog[4].description

        game.playerDetails.findAll { it.key != PONE.id }.each {
            ObjectId id, TBPlayerState state ->

                assert state.actionLog[-1].description == "1 deployed an ECM."
                assert state.actionLog[-1].actionType == TBActionLogEntry.TBActionType.UsedECM
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
