package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipToRepairAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate

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
        TBGame game = new TBGame()
        assert 1 == handler.movesRequired(game)
        game.features.add(GameFeature.PerShip)
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
        shouldFail(NoShipToRepairAtCoordinateException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1))
        })

    }
}
