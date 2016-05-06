package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 5/5/16
 * Time: 8:43 PM
 */
@CompileStatic
@Component
class CruiseMissileHandler extends AbstractSpecialMoveHandler {
    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        return null
    }

    @Override
    void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]

        if (playerState.cruiseMissilesRemaining < 1) {
            throw new NoCruiseMissileActionsRemaining()
        }

        //  Will not prevent you from firing on empty spaces if you want to waste it
    }
}
