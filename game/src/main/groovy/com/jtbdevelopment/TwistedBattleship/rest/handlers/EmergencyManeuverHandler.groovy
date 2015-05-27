package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.FogCoordinatesGenerator
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.ShipRelocator
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/21/15
 * Time: 6:39 AM
 */
@CompileStatic
@Component
class EmergencyManeuverHandler extends AbstractSpecialMoveHandler {
    @Autowired
    ShipRelocator shipRelocator

    @Autowired
    FogCoordinatesGenerator fogCoordinatesGenerator

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
        TBPlayerState playerState = game.playerDetails[player.id]
        ShipState ship = playerState.coordinateShipMap[coordinate]

        List<GridCoordinate> newCoordinates = shipRelocator.relocateShip(game, playerState, ship)
        Set<GridCoordinate> fogCoordinates = fogCoordinatesGenerator.generateFogCoordinates(game, ship.shipGridCells, newCoordinates)
        ship.shipGridCells = newCoordinates
        playerState.coordinateShipMap.clear()
        String message = player.displayName + " performed emergency maneuvers."
        playerState.lastActionMessage = message
        fogGrids(game, player, playerState, message, fogCoordinates)

        return game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<ObjectId, TBPlayerState> fogGrids(
            final TBGame game,
            final Player<ObjectId> player,
            final TBPlayerState playerState,
            final String message,
            final Set<GridCoordinate> fogCoordinates) {
        game.playerDetails.findAll { it.key != player.id }.each {
            ObjectId opponent, TBPlayerState opponentState ->
                opponentState.lastActionMessage = message
                Grid opponentGrid = opponentState.opponentGrids[player.id]
                Grid opponentView = playerState.opponentViews[opponent]
                fogCoordinates.each {
                    GridCoordinate fog ->
                        switch (opponentGrid.get(fog)) {
                            case GridCellState.KnownShip:
                                opponentGrid.set(fog, GridCellState.ObscuredShip)
                                opponentView.set(fog, GridCellState.ObscuredShip)
                                break;
                            case GridCellState.KnownByHit:
                                opponentGrid.set(fog, GridCellState.ObscuredHit)
                                opponentView.set(fog, GridCellState.ObscuredHit)
                                break;
                            case GridCellState.KnownByOtherHit:
                                opponentGrid.set(fog, GridCellState.ObscuredOtherHit)
                                opponentView.set(fog, GridCellState.ObscuredOtherHit)
                                break;
                            case GridCellState.KnownByRehit:
                                opponentGrid.set(fog, GridCellState.ObscuredRehit)
                                opponentView.set(fog, GridCellState.ObscuredRehit)
                                break;
                            case GridCellState.KnownByMiss:
                                opponentGrid.set(fog, GridCellState.ObscuredMiss)
                                opponentView.set(fog, GridCellState.ObscuredMiss)
                                break;
                            case GridCellState.KnownByOtherMiss:
                                opponentGrid.set(fog, GridCellState.ObscuredOtherMiss)
                                opponentView.set(fog, GridCellState.ObscuredOtherMiss)
                                break;
                            case GridCellState.KnownEmpty:
                                opponentGrid.set(fog, GridCellState.ObscuredEmpty)
                                opponentView.set(fog, GridCellState.ObscuredEmpty)
                                break;
                        }
                }
        }
    }
}
