package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipToRepairAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 5/19/15
 * Time: 6:36 AM
 */
@Component
@CompileStatic
class RepairShipHandler extends AbstractSpecialMoveHandler {
    @Override
    boolean targetSelf() {
        return true
    }

    @Override
    void validateMoveSpecific(
            final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[(ObjectId) player.id]

        if (playerState.emergencyRepairsRemaining < 1) {
            throw new NoRepairActionsRemainException()
        }

        //  Will let you repair an undamaged ship
        if (!playerState.coordinateShipMap.containsKey(coordinate)) {
            throw new NoShipToRepairAtCoordinateException()
        }
    }

    @Override
    TBGame playMove(
            final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate) {
        game
    }

}
