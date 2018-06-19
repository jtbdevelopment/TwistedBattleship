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
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import org.bson.types.ObjectId

/**
 * Date: 5/7/15
 * Time: 8:21 PM
 */
abstract class AbstractPlayerMoveHandler
        extends AbstractGameActionHandler<Target, ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {
    abstract boolean targetSelf()

    abstract int movesRequired(final TBGame game)

    abstract TBGame playMove(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate)

    abstract void validateMoveSpecific(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate);

    AbstractPlayerMoveHandler(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
            final GameTransitionEngine<TBGame> transitionEngine,
            final GamePublisher<TBGame, MongoPlayer> gamePublisher,
            final GameEligibilityTracker gameTracker,
            final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker)
    }

    @Override
    protected TBGame handleActionInternal(
            final MongoPlayer player, final TBGame game, final Target target) {

        Player targetPlayer = loadPlayerMD5(target.player)
        validateMove(player, game, targetPlayer, target.coordinate)
        return markHiddenHits(playMove(player, game, targetPlayer, target.coordinate), player)
    }

    //  TODO - unit test
    @SuppressWarnings("GrMethodMayBeStatic")
    protected TBGame markHiddenHits(TBGame game, MongoPlayer player) {
        TBPlayerState state = game.playerDetails[(ObjectId) player.id]
        state.shipStates.each {
            ShipState shipState ->
                for (int shipCell = 0; shipCell < shipState.ship.gridSize; ++shipCell) {
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
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
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
