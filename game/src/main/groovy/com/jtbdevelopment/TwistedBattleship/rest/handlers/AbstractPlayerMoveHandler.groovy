package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotTargetInactivePlayerException
import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException
import com.jtbdevelopment.TwistedBattleship.exceptions.InvalidTargetPlayerException
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId

/**
 * Date: 5/7/15
 * Time: 8:21 PM
 */
@CompileStatic
abstract class AbstractPlayerMoveHandler extends AbstractGameActionHandler<Target, TBGame> {
    abstract boolean targetSelf()

    abstract int movesRequired(final TBGame game)

    abstract TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate)

    abstract void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate);

    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final Target target) {

        Player targetPlayer = loadPlayerMD5(target.player)
        validateMove(player, game, targetPlayer, target.coordinate)
        return markHiddenHits(playMove(player, game, targetPlayer, target.coordinate), player)
    }

    //  TODO - unit test
    @SuppressWarnings("GrMethodMayBeStatic")
    protected TBGame markHiddenHits(TBGame game, Player player) {
        TBPlayerState state = game.playerDetails[(ObjectId) player.id]
        state.shipStates.each {
            Ship ship, ShipState shipState ->
                for (int shipCell = 0; shipCell < ship.gridSize; ++shipCell) {
                    if (shipState.shipSegmentHit[shipCell]) {
                        state.opponentViews.each {
                            ObjectId opponent, Grid opponentView ->
                                GridCoordinate shipCellCoordinate = shipState.shipGridCells[shipCell]
                                if (opponentView.get(shipCellCoordinate).rank < GridCellState.HiddenHit.rank) {
                                    opponentView.set(shipCellCoordinate, GridCellState.HiddenHit)
                                }
                        }
                    }
                }
        }
        return game
    }

    @Override
    protected TBGame rotateTurnBasedGame(final TBGame game) {
        game.remainingMoves -= movesRequired(game)
        if (game.remainingMoves == 0) {
            if (game.playerDetails.findAll { ObjectId id, TBPlayerState state -> state.alive }.size() > 1) {
                int initialIndex = game.players.findIndexOf { Player p -> p.id == game.currentPlayer }
                int newIndex = initialIndex
                boolean found = false
                while (!found) {
                    ++newIndex
                    if (newIndex == game.players.size()) {
                        newIndex = 0
                    }

                    Player nextPlayer = game.players[newIndex]
                    if (game.playerDetails[nextPlayer.id].alive) {
                        game.currentPlayer = nextPlayer.id
                        found = true
                    }
                }
                if (game.features.contains(GameFeature.PerShip)) {
                    game.remainingMoves = game.playerDetails[game.currentPlayer].activeShipsRemaining
                } else {
                    game.remainingMoves = 1
                }
            }
        }
        return game
    }

    private void validateMove(
            final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate) {
        if (game.currentPlayer != player.id) {
            throw new PlayerOutOfTurnException()
        }

        validatePlayerForGame(game, targetPlayer)

        if (targetSelf() != (player == targetPlayer)) {
            throw new InvalidTargetPlayerException()
        }

        if (game.remainingMoves < movesRequired(game)) {
            throw new NotEnoughActionsForSpecialException()
        }

        if (!coordinate.isValidCoordinate(game)) {
            throw new CoordinateOutOfBoundsException()
        }

        if (game.gamePhase != GamePhase.Playing) {
            throw new GameIsNotInPlayModeException()
        }

        if (!game.playerDetails[(ObjectId) targetPlayer.id].alive) {
            throw new CannotTargetInactivePlayerException()
        }
        validateMoveSpecific(player, game, targetPlayer, coordinate)
    }
}
