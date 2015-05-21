package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 5/21/15
 * Time: 6:39 AM
 */
@CompileStatic
@Component
class EmergencyManeuverHandler extends AbstractSpecialMoveHandler {
    @Override
    boolean targetSelf() {
        return true
    }

    @Override
    void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {

        def state = game.playerDetails[player.id]
        if (state.evasiveManeuversRemaining < 1) {
            throw new NoEmergencyManeuverActionsRemainException()
        }

        if (!state.coordinateShipMap.containsKey(coordinate)) {
            throw new NoShipAtCoordinateException()
        }
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        return null
    }

}
