package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 5/11/15
 * Time: 7:08 PM
 */

//  TODO - criticals
//  TODO - breakup?
@Component
@CompileStatic
class FireAtCoordinateHandler extends AbstractPlayerMoveHandler {

    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    int movesRequired(final TBGame game) {
        return 1
    }

    @Override
    void validateMoveSpecific(
            final Player player, final TBGame game, final Player targetPlayer, final GridCoordinate coordinate) {
        //
    }

    @Override
    TBGame playMove(
            final Player player, final TBGame game, final Player targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState targetedState = game.playerDetails[(ObjectId) targetedPlayer.id]
        TBPlayerState playerState = game.playerDetails[(ObjectId) player.id]
        ShipState ship = targetedState.coordinateShipMap[coordinate]
        if (ship) {
            int hitIndex = ship.shipGridCells.indexOf(coordinate)
            if (ship.shipSegmentHit[hitIndex]) {
                processReHit(game, player, playerState, targetedPlayer, targetedState, coordinate, ship)
            } else {
                processHit(game, player, playerState, targetedPlayer, targetedState, coordinate, ship, hitIndex)
            }
        } else {
            processMiss(game, player, playerState, targetedPlayer, targetedState, coordinate)
        }
        game
    }

    protected void processMiss(
            final TBGame game,
            final Player player,
            final TBPlayerState playerState,
            final Player targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate) {
        playerState.lastActionMessage = "No enemy at " + coordinate + "."
        targetedState.lastActionMessage = player.displayName + " missed at " + coordinate + "."
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByMiss, GridCellState.KnownByOtherMiss)
    }

    protected void processHit(
            final TBGame game,
            final Player player,
            final TBPlayerState playerState,
            final Player targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate,
            final ShipState ship,
            final int hitIndex) {
        ship.shipSegmentHit[hitIndex] = true
        ship.healthRemaining -= 1
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByHit, GridCellState.KnownByOtherHit)

        playerState.scoreFromHits += TBGameScorer.SCORE_FOR_HIT
        playerState.lastActionMessage = "Direct hit at " + coordinate + "!"
        targetedState.lastActionMessage = player.displayName + " hit your " + ship.ship.description + " at " + coordinate + "!"
        checkForShipSinking(game, player, playerState, targetedPlayer, targetedState, ship)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void checkForShipSinking(
            final TBGame game,
            final Player player,
            final TBPlayerState playerState,
            final Player targetedPlayer,
            final TBPlayerState targetedState,
            final ShipState ship) {
        if (ship.healthRemaining == 0) {
            playerState.lastActionMessage = "You sunk a " + ship.ship.description + "!"
            targetedState.lastActionMessage = player.displayName + " sunk your " + ship.ship.description + "!"
            game.generalMessage = player.displayName + " sunk " + targetedPlayer.displayName + "'s " + ship.ship.description + "!"
            playerState.scoreFromSinks += TBGameScorer.SCORE_FOR_SINK
            if (!targetedState.alive) {
                game.generalMessage = player.displayName + " has defeated " + targetedPlayer.displayName + "!"
            }
        }
    }

    protected void processReHit(
            final TBGame game,
            final Player player,
            final TBPlayerState playerState,
            final Player targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate,
            final ShipState ship) {
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByRehit, GridCellState.KnownByOtherHit)
        playerState.lastActionMessage = "Damaged area hit again at " + coordinate + "."
        targetedState.lastActionMessage = player.displayName + " re-hit your " + ship.ship.description + " at " + coordinate + "."
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void markGrids(
            final TBGame game,
            final Player player,
            final Player targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate,
            final GridCellState markForPlayer,
            final GridCellState markForOthers) {
        boolean sharedState = game.features.contains(GameFeature.SharedIntel)
        game.playerDetails.each {
            ObjectId playerId, TBPlayerState state ->
                switch (playerId) {
                    case player.id:
                        state.opponentGrids[(ObjectId) targetedPlayer.id].set(coordinate, markForPlayer)
                        break
                    case targetedPlayer.id:
                        state.opponentViews[(ObjectId) player.id].set(coordinate, markForPlayer)
                        break
                    default:
                        if (sharedState) {
                            if (state.opponentGrids[(ObjectId) targetedPlayer.id].get(coordinate).rank < markForOthers.rank) {
                                state.opponentGrids[(ObjectId) targetedPlayer.id].set(coordinate, markForOthers)
                                targetedState.opponentViews[playerId].set(coordinate, markForOthers)
                            }

                        }
                }
        }
        /*
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
        */
    }
}
