package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate

/**
 * Date: 5/21/15
 * Time: 6:41 AM
 */
class EmergencyManeuverHandlerTest extends AbstractBaseHandlerTest {
    EmergencyManeuverHandler handler = new EmergencyManeuverHandler()

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
        game.playerDetails[PONE.id].evasiveManeuversRemaining = 1
        handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        game.playerDetails[PONE.id].evasiveManeuversRemaining = 0
        shouldFail(NoEmergencyManeuverActionsRemainException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 0))
        })
    }

    void testValidatesShipExistsAtCoordinate() {
        game.playerDetails[PONE.id].emergencyRepairsRemaining = 1
        shouldFail(NoShipAtCoordinateException.class, {
            handler.validateMoveSpecific(PONE, game, PONE, new GridCoordinate(3, 1))
        })
    }

}
