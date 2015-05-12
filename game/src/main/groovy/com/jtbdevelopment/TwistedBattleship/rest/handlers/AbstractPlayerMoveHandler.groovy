package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException
import com.jtbdevelopment.TwistedBattleship.rest.Target
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
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

    abstract int movesRequired()

    abstract TBGame playMove(final Player player, final TBGame game, final Target target)

    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final Target target) {

        validateMove(player, game, target)
        return playMove(player, game, target)
    }

    @Override
    protected TBGame rotateTurnBasedGame(final TBGame game) {
        game.remainingMoves -= movesRequired()
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

    private void validateMove(final Player player, final TBGame game, final Target target) {
        if (game.currentPlayer != player.id) {
            throw new PlayerOutOfTurnException()
        }
        validatePlayerForGame(game, loadPlayerMD5s([target.player])[0])
        if (game.remainingMoves < movesRequired()) {
            throw new NotEnoughActionsForSpecialException()
        }
        if (!gridSizeUtil.isValidCoordinate(game, target.coordinate)) {
            throw new CoordinateOutOfBoundsException()
        }
        if (game.gamePhase != GamePhase.Playing) {
            throw new GameIsNotInPlayModeException()
        }
    }
}
