package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipToRepairAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
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
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]

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
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]
        ShipState state = playerState.coordinateShipMap[coordinate]

        String message = player.displayName + " repaired their " + state.ship.description + "."
        playerState.lastActionMessage = message

        --playerState.emergencyRepairsRemaining

        game.playerDetails.findAll { it.key != player.id }.each {
            ObjectId id, TBPlayerState opponent ->
                Grid opponentGrid = opponent.opponentGrids[player.id]
                Grid opponentView = playerState.opponentViews[id]
                state.shipGridCells.each {
                    GridCoordinate shipCoordinate ->
                        switch (opponentGrid.get(shipCoordinate)) {
                            case GridCellState.KnownByHit:
                            case GridCellState.KnownByRehit:
                            case GridCellState.KnownByOtherHit:
                                opponentGrid.set(shipCoordinate, GridCellState.KnownShip)
                                opponentView.set(shipCoordinate, GridCellState.KnownShip)
                                break;
                        }
                }

                opponent.lastActionMessage = message
        }
        state.healthRemaining = state.ship.gridSize
        state.shipSegmentHit = (1..state.healthRemaining).collect { false }
        game
    }

}