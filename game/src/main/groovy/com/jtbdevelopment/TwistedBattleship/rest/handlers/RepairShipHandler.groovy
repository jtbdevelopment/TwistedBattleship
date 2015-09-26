package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotRepairADestroyedShipException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
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
            throw new NoShipAtCoordinateException()
        }

        if(playerState.coordinateShipMap[coordinate].healthRemaining == 0) {
            throw new CannotRepairADestroyedShipException()
        }
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]
        ShipState state = playerState.coordinateShipMap[coordinate]

        String message = player.displayName + " repaired their " + state.ship.description + "."

        TBActionLogEntry actionLogEntry = new TBActionLogEntry(actionType: TBActionLogEntry.TBActionType.Repaired, description: message)
        playerState.actionLog.add(actionLogEntry)

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
                        if (opponentView.get(shipCoordinate) == GridCellState.HiddenHit) {
                            opponentView.set(shipCoordinate, GridCellState.Unknown)
                        }
                }
                opponent.actionLog.add(actionLogEntry)
        }
        state.healthRemaining = state.ship.gridSize
        state.shipSegmentHit = (1..state.healthRemaining).collect { false }
        game
    }

}
