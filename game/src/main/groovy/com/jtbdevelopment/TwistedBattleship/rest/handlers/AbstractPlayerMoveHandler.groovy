package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException
import com.jtbdevelopment.TwistedBattleship.exceptions.InvalidTargetPlayerException
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.grid.GridSizeUtil
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired

/**
 * Date: 5/7/15
 * Time: 8:21 PM
 */
@CompileStatic
abstract class AbstractPlayerMoveHandler extends AbstractGameActionHandler<Target, TBGame> {
    @Autowired
    GridSizeUtil gridSizeUtil

    abstract boolean targetSelf()

    abstract int movesRequired(final TBGame game)

    abstract TBGame playMove(
            final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate)

    abstract void validateMoveSpecific(
            final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate);

    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final Target target) {

        Player targetPlayer = loadPlayerMD5(target.player)
        validateMove(player, game, targetPlayer, target.coordinate)
        return playMove(player, game, targetPlayer, target.coordinate)
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
                game.remainingMoves = game.movesPerTurn
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

        if (!gridSizeUtil.isValidCoordinate(game, coordinate)) {
            throw new CoordinateOutOfBoundsException()
        }

        if (game.gamePhase != GamePhase.Playing) {
            throw new GameIsNotInPlayModeException()
        }
        validateMoveSpecific(player, game, targetPlayer, coordinate)
    }
}
