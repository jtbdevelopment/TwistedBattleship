package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 5/15/15
 * Time: 6:55 AM
 */
@CompileStatic
@Component
class SpyHandler extends AbstractPlayerMoveHandler {
    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    int movesRequired(final TBGame game) {
        return game.features.contains(GameFeature.PerShip) ? 2 : 1
    }

    @Override
    void validateMoveSpecific(
            final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate) {
        if (game.playerDetails[(ObjectId) player.id].spysRemaining < 1) {
            throw new NoSpyActionsRemainException()
        }
    }

    @Override
    TBGame playMove(
            final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate) {

        return null
    }
}
