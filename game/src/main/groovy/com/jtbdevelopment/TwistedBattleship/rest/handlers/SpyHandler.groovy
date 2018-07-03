package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.dao.AbstractGameRepository
import com.jtbdevelopment.games.dao.AbstractPlayerRepository
import com.jtbdevelopment.games.events.GamePublisher
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.state.masking.GameMasker
import com.jtbdevelopment.games.state.transition.GameTransitionEngine
import com.jtbdevelopment.games.tracking.GameEligibilityTracker
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 5/15/15
 * Time: 6:55 AM
 */
@CompileStatic
@Component
class SpyHandler extends AbstractSpecialMoveHandler {
    @Autowired
    GridCircleUtil gridCircleUtil

    SpyHandler(AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, GameTransitionEngine<TBGame> transitionEngine, GamePublisher<TBGame, MongoPlayer> gamePublisher, GameEligibilityTracker gameTracker, GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker)
    }

    @Override
    boolean targetSelf() {
        return false
    }

    @Override
    void validateMoveSpecific(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        if (game.playerDetails[player.id].spysRemaining < 1) {
            throw new NoSpyActionsRemainException()
        }
    }

    @Override
    TBGame playMove(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        Set<GridCoordinate> coordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate)
        Map<GridCoordinate, GridCellState> spyResults = computeSpyCoordinateStates(game, targetedPlayer, coordinates)
        updatePlayerGrids(game, player, targetedPlayer, spyResults, coordinate)
        --game.playerDetails[player.id].spysRemaining
        return game
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected void updatePlayerGrids(
            final TBGame game,
            final MongoPlayer player,
            final MongoPlayer targetedPlayer,
            final Map<GridCoordinate, GridCellState> spyResults,
            final GridCoordinate targetCoordinate) {
        game.playerDetails[player.id].actionLog.add(new TBActionLogEntry(
                TBActionLogEntry.TBActionType.Spied,
                "You spied on " + targetedPlayer.displayName + " at " + targetCoordinate + ".",
        )
        )
        game.playerDetails[targetedPlayer.id].actionLog.add(new TBActionLogEntry(
                TBActionLogEntry.TBActionType.Spied,
                player.displayName + " spied on you at " + targetCoordinate + ".",
        )
        )
        boolean sharedIntel = game.features.contains(GameFeature.SharedIntel)
        String messageForOthers = player.displayName + " spied on " + targetedPlayer.displayName + " at " + targetCoordinate + "."
        game.playerDetails.findAll {
            ObjectId id, TBPlayerState state ->
                id != targetedPlayer.id && (sharedIntel || id == player.id)
        }.each {
            ObjectId id, TBPlayerState state ->
                Grid playerGrid = state.opponentGrids[targetedPlayer.id]
                Grid targetView = game.playerDetails[targetedPlayer.id].opponentViews[id]
                updateGridForPlayer(playerGrid, targetView, spyResults)
                if (id != player.id) {
                    state.actionLog.add(0,
                            new TBActionLogEntry(
                                    TBActionLogEntry.TBActionType.Spied,
                                    messageForOthers,
                            )
                    )
                }
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<GridCoordinate, GridCellState> updateGridForPlayer(
            final Grid playerGrid,
            final Grid targetView,
            final Map<GridCoordinate, GridCellState> spyResults) {
        spyResults.each {
            GridCoordinate c, GridCellState cs ->
                if (playerGrid.get(c).rank < cs.rank) {
                    playerGrid.set(c, cs)
                    targetView.set(c, cs)
                }
        }
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    protected Map<GridCoordinate, GridCellState> computeSpyCoordinateStates(
            final TBGame game, final MongoPlayer targetedPlayer, final Set<GridCoordinate> coordinates) {
        TBPlayerState targetedState = game.playerDetails[targetedPlayer.id]
        coordinates.collectEntries {
            GridCoordinate targetCoordinate ->
                GridCellState state
                ShipState shipState = targetedState.coordinateShipMap[targetCoordinate]
                if (shipState) {
                    if (shipState.shipSegmentHit[shipState.shipGridCells.indexOf(targetCoordinate)]) {
                        state = GridCellState.KnownByOtherHit
                    } else {
                        state = GridCellState.KnownShip
                    }
                } else {
                    state = GridCellState.KnownEmpty
                }
                [(targetCoordinate): state]
        }
    }
}
