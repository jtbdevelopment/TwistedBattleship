package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/15/15
 * Time: 6:55 AM
 */
@CompileStatic
@Component
class SpyHandler extends AbstractSpecialMoveHandler {
    @Autowired
    GridCircleUtil gridCircleUtil

    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        if (game.playerDetails[player.id].spysRemaining < 1) {
            throw new NoSpyActionsRemainException()
        }
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        Collection<GridCoordinate> coordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate)
        Map<GridCoordinate, GridCellState> spyResults = computeSpyCoordinateStates(game, targetedPlayer, coordinates)
        updatePlayerGrids(game, player, targetedPlayer, spyResults, coordinate)
        --game.playerDetails[player.id].spysRemaining
        return game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void updatePlayerGrids(
            final TBGame game,
            final Player<ObjectId> player,
            final Player<ObjectId> targetedPlayer,
            final Map<GridCoordinate, GridCellState> spyResults,
            final GridCoordinate targetCoordinate) {
        String message = player.displayName + " spied on " + targetedPlayer.displayName + " at " + targetCoordinate + "."
        boolean sharedIntel = game.features.contains(GameFeature.SharedIntel)
        game.playerDetails.findAll {
            ObjectId id, TBPlayerState state ->
                id != targetedPlayer.id && (sharedIntel || id == player.id)
        }.each {
            ObjectId id, TBPlayerState state ->
                Grid playerGrid = state.opponentGrids[targetedPlayer.id]
                Grid targetView = game.playerDetails[targetedPlayer.id].opponentViews[id]
                updateGridForPlayer(playerGrid, targetView, spyResults)
                state.lastActionMessage = message
        }
        game.playerDetails[targetedPlayer.id].lastActionMessage = message
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<GridCoordinate, GridCellState> updateGridForPlayer(
            final Grid playerGrid,
            final Grid targetView,
            final Map<GridCoordinate, GridCellState> spyResults) {
        spyResults.each {
            GridCoordinate c, GridCellState cs ->
                if (playerGrid.get(c).rank < cs.rank) {
                    playerGrid.set(c, cs)
                    targetView.set(c, cs)
                }
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<GridCoordinate, GridCellState> computeSpyCoordinateStates(
            final TBGame game, final Player<ObjectId> targetedPlayer, final Collection<GridCoordinate> coordinates) {
        TBPlayerState targetedState = game.playerDetails[targetedPlayer.id]
        coordinates.collectEntries {
            GridCoordinate targetCoordinate ->
                GridCellState state
                ShipState shipState = targetedState.coordinateShipMap[targetCoordinate]
                if (shipState) {
                    if (shipState.shipSegmentHit[shipState.shipGridCells.indexOf(targetCoordinate)]) {
                        state = GridCellState.KnownByOtherHit
                    } else {
                        state = GridCellState.KnownShip
                    }
                } else {
                    state = GridCellState.KnownEmpty
                }
                [(targetCoordinate): state]
        }
    }
}
