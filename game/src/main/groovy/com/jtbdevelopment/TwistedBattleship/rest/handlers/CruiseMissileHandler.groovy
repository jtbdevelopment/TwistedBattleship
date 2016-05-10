package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/5/16
 * Time: 8:43 PM
 */
@CompileStatic
@Component
class CruiseMissileHandler extends AbstractSpecialMoveHandler {
    @Autowired
    FireAtCoordinateHandler fireAtCoordinateHandler

    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    TBGame playMove(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]
        TBPlayerState targetState = game.playerDetails[targetedPlayer.id]
        ShipState shipState = targetState.coordinateShipMap[coordinate]

        generateLogEntries(game, player, targetedPlayer, coordinate, shipState)

        --playerState.cruiseMissilesRemaining
        if (shipState) {
            shipState.shipGridCells.each {
                GridCoordinate shipCoordinate ->
                    fireAtCoordinateHandler.playMove(player, game, targetedPlayer, shipCoordinate)
            }
        } else {
            fireAtCoordinateHandler.playMove(player, game, targetedPlayer, coordinate)
        }
        game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void generateLogEntries(TBGame game, Player<ObjectId> player, Player<ObjectId> targetedPlayer, GridCoordinate coordinate, ShipState shipState) {
        boolean sharedState = game.features.contains(GameFeature.SharedIntel)
        String playerMessage = "You fired a cruise missile at " + targetedPlayer.displayName + " " + coordinate + " and "
        String targetMessage = player.displayName + " fired a cruise missile at " + coordinate + " and "
        String otherMessage = player.displayName + " fired a cruise missile at " + targetedPlayer.displayName
        if (sharedState) otherMessage += " " + coordinate + " and "
        if (shipState) {
            playerMessage += "hit!"
            targetMessage += "hit!"
            if (sharedState) otherMessage += "hit!" else otherMessage += "."
        } else {
            playerMessage += "missed."
            targetMessage += "missed."
            if (sharedState) otherMessage += "missed." else otherMessage += "."
        }
        game.playerDetails.forEach {
            ObjectId playerId, TBPlayerState state ->
                switch (playerId) {
                    case player.id:
                        state.actionLog.add(new TBActionLogEntry(actionType: TBActionLogEntry.TBActionType.CruiseMissile, description: playerMessage))
                        break
                    case targetedPlayer.id:
                        state.actionLog.add(new TBActionLogEntry(actionType: TBActionLogEntry.TBActionType.CruiseMissile, description: targetMessage))
                        break
                    default:
                        if (playerId != player.id && playerId != targetedPlayer.id) {
                            state.actionLog.add(new TBActionLogEntry(actionType: TBActionLogEntry.TBActionType.CruiseMissile, description: otherMessage))
                        }
                        break
                }
        }
    }

    @Override
    void validateMoveSpecific(
            final Player<ObjectId> player,
            final TBGame game, final Player<ObjectId> targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.playerDetails[player.id]

        if (playerState.cruiseMissilesRemaining < 1) {
            throw new NoCruiseMissileActionsRemaining()

        }

        //  Will not prevent you from firing on empty spaces if you want to waste it
    }
}
