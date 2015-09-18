package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotRepairADestroyedShipException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship

/**
 * Date: 5/19/15
 * Time: 6:39 AM
 */
class RepairShipHandlerTest extends AbstractBaseHandlerTest {
    RepairShipHandler handler = new RepairShipHandler()

    void testTargetSelf() {
        assert handler.targetSelf()
    }

    void testMovesRequired() {
        TBGame game = new TBGame(movesForSpecials: 1)
        assert 1 == handler.movesRequired(game)
        game = new TBGame(movesForSpecials: 2)
        assert 2 == handler.movesRequired(game)
    }

    void testValidatesRepairsRemain() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 0
        shouldFail(NoRepairActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        })
    }

    void testValidatesShipExistsAtCoordinate() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        shouldFail(NoShipAtCoordinateException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1))
        })
    }

    void testRepair() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        game.playerDetails[PONE.id].shipStates[Ship.Carrier].healthRemaining = 2
        game.playerDetails[PONE.id].shipStates[Ship.Carrier].shipSegmentHit = [false, true, true, true, false]
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByRehit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(3, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(4, 0, GridCellState.Unknown)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByRehit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(3, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(4, 0, GridCellState.Unknown)

        game.playerDetails[PTHREE.id].opponentGrids[PONE.id].set(3, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(3, 0, GridCellState.KnownByHit)

        assert game.is(handler.playMove(PONE, game, PONE, new GridCoordinate(4, 0)))

        assert GridCellState.KnownShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(0, 0)
        assert GridCellState.KnownShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(1, 0)
        assert GridCellState.KnownShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(2, 0)
        assert GridCellState.KnownShip == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(3, 0)
        assert GridCellState.Unknown == game.playerDetails[PTWO.id].opponentGrids[PONE.id].get(4, 0)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(0, 0)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(1, 0)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(2, 0)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(3, 0)
        assert GridCellState.Unknown == game.playerDetails[PONE.id].opponentViews[PTWO.id].get(4, 0)
        assert GridCellState.KnownShip == game.playerDetails[PTHREE.id].opponentGrids[PONE.id].get(3, 0)
        assert GridCellState.KnownShip == game.playerDetails[PONE.id].opponentViews[PTHREE.id].get(3, 0)
        game.playerDetails.each {
            assert "1 repaired their Aircraft Carrier." == it.value.lastActionMessage
        }
        assert 0 == game.playerDetails[PONE.id].emergencyRepairsRemaining
        assert 5 == game.playerDetails[PONE.id].shipStates[Ship.Carrier].healthRemaining
        assert [false, false, false, false, false] == game.playerDetails[PONE.id].shipStates[Ship.Carrier].shipSegmentHit
    }

    void testRepairOnDestroyedShip() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        game.playerDetails[PONE.id].shipStates[Ship.Carrier].healthRemaining = 0
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(2, 0, GridCellState.KnownByRehit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(3, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PTWO.id].opponentGrids[PONE.id].set(4, 0, GridCellState.Unknown)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(0, 0, GridCellState.KnownShip)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(1, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(2, 0, GridCellState.KnownByRehit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(3, 0, GridCellState.KnownByOtherHit)
        game.playerDetails[PONE.id].opponentViews[PTWO.id].set(4, 0, GridCellState.Unknown)

        game.playerDetails[PTHREE.id].opponentGrids[PONE.id].set(3, 0, GridCellState.KnownByHit)
        game.playerDetails[PONE.id].opponentViews[PTHREE.id].set(3, 0, GridCellState.KnownByHit)

        shouldFail(CannotRepairADestroyedShipException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(4, 0))

        });
    }
}
