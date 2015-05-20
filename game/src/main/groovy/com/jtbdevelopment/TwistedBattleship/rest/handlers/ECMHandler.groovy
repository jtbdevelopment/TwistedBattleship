package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/20/15
 * Time: 6:35 PM
 */
@Component
@CompileStatic
class ECMHandler extends AbstractSpecialMoveHandler {
    @Autowired
    GridCircleUtil gridCircleUtil

    @Override
    boolean targetSelf() {
        return true
    }

    @Override
    void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        if (game.playerDetails[player.id].ecmsRemaining < 1) {
            throw new NoECMActionsRemainException()
        }

    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState state = game.playerDetails[player.id]
        String message = player.displayName + " deployed an ECM at " + coordinate + "."

        --state.ecmsRemaining
        state.lastActionMessage = message

        Set<GridCoordinate> ecmCoordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate)

        game.playerDetails.findAll { it.key != player.id }.each {
            ObjectId pid, TBPlayerState opponent ->
                opponent.lastActionMessage = message
                Grid opponentGrid = opponent.opponentGrids[player.id]
                Grid opponentView = state.opponentViews[pid]
                ecmCoordinates.each {
                    opponentGrid.set(it, GridCellState.Unknown)
                    opponentView.set(it, GridCellState.Unknown)
                }
        }

        game
    }

}
