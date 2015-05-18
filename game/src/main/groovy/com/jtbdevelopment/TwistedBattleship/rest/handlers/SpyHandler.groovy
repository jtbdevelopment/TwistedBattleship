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
    protected final static Map<Integer, List<GridCoordinate>> SPY_CIRCLE = [
            (10): [
                    new GridCoordinate(0, -1),
                    new GridCoordinate(0, 0),
                    new GridCoordinate(0, 1),
                    new GridCoordinate(1, 0),
                    new GridCoordinate(-1, 0),
            ],  // 5, of 100 = 5%
            (15): [
                    new GridCoordinate(0, -2),
                    new GridCoordinate(0, 2),
                    new GridCoordinate(2, 0),
                    new GridCoordinate(-2, 0),
                    new GridCoordinate(1, 1),
                    new GridCoordinate(1, -1),
                    new GridCoordinate(-1, -1),
                    new GridCoordinate(-1, 1),
            ],  // 13, of 225 = 5.7%
            (20): [
                    new GridCoordinate(2, 2),
                    new GridCoordinate(2, -2),
                    new GridCoordinate(-2, -2),
                    new GridCoordinate(-2, 2),
                    new GridCoordinate(2, 1),
                    new GridCoordinate(1, 2),
                    new GridCoordinate(2, -1),
                    new GridCoordinate(-1, 2),
                    new GridCoordinate(-2, -1),
                    new GridCoordinate(-1, -2),
                    new GridCoordinate(-2, 1),
                    new GridCoordinate(1, -2),
            ],  // 25 of 400 = 6.25%
    ]

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
        int size = gridSizeUtil.getSize(game)

        Collection<GridCoordinate> coordinates = SPY_CIRCLE.findAll {
            it.key <= size
        }.collectMany {
            int listSize, List<GridCoordinate> adjustments ->
                adjustments.collect {
                    GridCoordinate adjustment ->
                        coordinate.add(adjustment)
                }
        }.findAll {
            GridCoordinate it -> gridSizeUtil.isValidCoordinate(game, it)
        }



        return null
    }
}
