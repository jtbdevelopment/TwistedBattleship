package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
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
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        //
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState targetedState = game.playerDetails[targetedPlayer.id]
        TBPlayerState playerState = game.playerDetails[player.id]
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
            final Player<ObjectId> player,
            final TBPlayerState playerState,
            final Player<ObjectId> targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate) {
        playerState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: "You fired at " + targetedPlayer.displayName + " " + coordinate + " and missed."
        ))
        targetedState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: player.displayName + " fired at " + coordinate + " and missed."
        ))
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(
                description: player.displayName + " fired at " + targetedPlayer.displayName + " " + coordinate + " and missed.",
                actionType: TBActionLogEntry.TBActionType.Fired
        )
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByMiss, GridCellState.KnownByOtherMiss, sharedLogEntry)
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
        playerState.scoreFromHits += TBGameScorer.SCORE_FOR_HIT
        playerState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: "You fired at " + targetedPlayer.displayName + " " + coordinate + " and hit!"
        ))
        targetedState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: player.displayName + " fired at " + coordinate + " and hit your " + ship.ship.description + "!"
        ))
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(
                description: player.displayName + " fired at " + targetedPlayer.displayName + " " + coordinate + " and hit!",
                actionType: TBActionLogEntry.TBActionType.Fired
        )
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByHit, GridCellState.KnownByOtherHit, sharedLogEntry)
        checkForShipSinking(game, player, playerState, targetedPlayer, targetedState, ship)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void checkForShipSinking(
            final TBGame game,
            final Player<ObjectId> player,
            final TBPlayerState playerState,
            final Player<ObjectId> targetedPlayer,
            final TBPlayerState targetedState,
            final ShipState ship) {
        if (ship.healthRemaining == 0) {
            targetedState.actionLog.add(new TBActionLogEntry(
                    actionType: TBActionLogEntry.TBActionType.Sunk,
                    description: player.displayName + " sunk your " + ship.ship.description + "!"
            ))
            playerState.actionLog.add(new TBActionLogEntry(
                    actionType: TBActionLogEntry.TBActionType.Sunk,
                    description: "You sunk a " + ship.ship.description + " for " + targetedPlayer.displayName + "!"
            ))
            if (game.features.contains(GameFeature.SharedIntel)) {
                TBActionLogEntry sharedLogEntry = new TBActionLogEntry(
                        description: player.displayName + " sunk a " + ship.ship.description + " for " + targetedPlayer.displayName + "!",
                        actionType: TBActionLogEntry.TBActionType.Sunk
                )
                game.playerDetails.findAll {
                    it.key != player.id && it.key != targetedPlayer.id
                }.each {
                    it.value.actionLog.add(sharedLogEntry)
                }
            }
            playerState.scoreFromSinks += TBGameScorer.SCORE_FOR_SINK
            if (!targetedState.alive) {
                TBActionLogEntry logEntry = new TBActionLogEntry(
                        actionType: TBActionLogEntry.TBActionType.Defeated,
                        description: targetedPlayer.displayName + " has been defeated!"
                )
                game.playerDetails.each {
                    it.value.actionLog.add(logEntry)
                }
            }
        }
    }

    protected void processReHit(
            final TBGame game,
            final Player<ObjectId> player,
            final TBPlayerState playerState,
            final Player<ObjectId> targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate,
            final ShipState ship) {
        playerState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: "You fired at " + targetedPlayer.displayName + " " + coordinate + " and hit an already damaged area!"
        ))
        targetedState.actionLog.add(new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.Fired,
                description: player.displayName + " fired at " + coordinate + " and re-hit your " + ship.ship.description + "!"
        ))
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(
                description: player.displayName + " fired at " + targetedPlayer.displayName + " " + coordinate + " and re-hit a damaged area!",
                actionType: TBActionLogEntry.TBActionType.Fired
        )
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByRehit, GridCellState.KnownByOtherHit, sharedLogEntry)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void markGrids(
            final TBGame game,
            final Player<ObjectId> player,
            final Player<ObjectId> targetedPlayer,
            final TBPlayerState targetedState,
            final GridCoordinate coordinate,
            final GridCellState markForPlayer,
            final GridCellState markForOthers,
            final TBActionLogEntry sharedLogEntry) {
        boolean sharedState = game.features.contains(GameFeature.SharedIntel)
        TBActionLogEntry nonSharedLogEntry = new TBActionLogEntry(
                description: player.displayName + " fired at " + targetedPlayer.displayName + ".",
                actionType: TBActionLogEntry.TBActionType.Fired
        )
        game.playerDetails.each {
            ObjectId playerId, TBPlayerState state ->
                switch (playerId) {
                    case player.id:
                        state.opponentGrids[targetedPlayer.id].set(coordinate, markForPlayer)
                        break
                    case targetedPlayer.id:
                        state.opponentViews[player.id].set(coordinate, markForPlayer)
                        break
                    default:
                        if (sharedState) {
                            if (state.opponentGrids[targetedPlayer.id].get(coordinate).rank < markForOthers.rank) {
                                state.opponentGrids[targetedPlayer.id].set(coordinate, markForOthers)
                                targetedState.opponentViews[playerId].set(coordinate, markForOthers)
                            }
                            state.actionLog.add(sharedLogEntry)
                        } else {
                            state.actionLog.add(nonSharedLogEntry)
                        }
                }
        }
    }
}
