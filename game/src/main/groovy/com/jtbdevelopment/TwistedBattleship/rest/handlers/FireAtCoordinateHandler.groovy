package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.ShipFinder
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/11/15
 * Time: 7:08 PM
 */

//  TODO - criticals
@Component
@CompileStatic
class FireAtCoordinateHandler extends AbstractPlayerMoveHandler {
    @Autowired
    ShipFinder shipFinder

    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    int movesRequired(final TBGame game) {
        return 1
    }

    @Override
    TBGame playMove(
            final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState targeted = game.playerDetails[(ObjectId) targetedPlayer.id]
        TBPlayerState playerState = game.playerDetails[(ObjectId) player.id]

        ShipState ship = shipFinder.findShipForCoordinate(targeted, coordinate)
        if (ship) {
            int index = ship.shipGridCells.indexOf(coordinate)
            if (ship.shipSegmentHit[index]) {
                markGrids(game, player, targetedPlayer, coordinate, GridCellState.KnownByRehit, GridCellState.KnownByOtherHit)
                playerState.lastActionMessage = "Damaged area hit again."
            } else {
                ship.shipSegmentHit[index] = true
                ship.healthRemaining -= 1
                markGrids(game, player, targetedPlayer, coordinate, GridCellState.KnownByHit, GridCellState.KnownByOtherHit)

                playerState.scoreFromHits += TBGameScorer.SCORE_FOR_HIT
                playerState.lastActionMessage = "Direct hit!"
                if (ship.healthRemaining == 0) {
                    playerState.lastActionMessage = "You sunk a " + ship.ship + "!"
                    playerState.scoreFromSinks += TBGameScorer.SCORE_FOR_SINK
                    if (!targeted.alive) {
                        game.generalMessage = player.displayName + " has defeated " + targetedPlayer.displayName + "!"
                    }
                }
            }
        } else {
            playerState.lastActionMessage = "Wasted ammo!"
            markGrids(game, player, targetedPlayer, coordinate, GridCellState.KnownByMiss, GridCellState.KnownByOtherMiss)
        }
        game
    }

    protected void markGrids(TBGame game, Player player, Player targetedPlayer, GridCoordinate coordinate, GridCellState markForPlayer, GridCellState markForOthers) {
        game.playerDetails[(ObjectId) player.id].opponentGrids[(ObjectId) targetedPlayer.id].set(coordinate, markForPlayer)
        game.playerDetails[(ObjectId) targetedPlayer.id].opponentViews[(ObjectId) player.id].set(coordinate, markForPlayer)
        if (game.features.contains(GameFeature.SharedIntel)) {
            game.playerDetails.findAll {
                it.key != targetedPlayer.id && it.key != player.id
            }.each {
                if (it.value.opponentGrids[(ObjectId) targetedPlayer.id].get(coordinate).rank < markForOthers.rank) {
                    it.value.opponentGrids[(ObjectId) targetedPlayer.id].set(coordinate, markForOthers)
                    game.playerDetails[(ObjectId) targetedPlayer.id].opponentViews[(ObjectId) it.key].set(coordinate, markForOthers)
                }
            }
        }
    }
}
