package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException
import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame
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
 * Date: 5/20/15
 * Time: 6:35 PM
 */
@Component
@CompileStatic
class ECMHandler extends AbstractSpecialMoveHandler {
    @Autowired
    GridCircleUtil gridCircleUtil

    ECMHandler(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
            final GameTransitionEngine<TBGame> transitionEngine,
            final GamePublisher<TBGame, MongoPlayer> gamePublisher,
            final GameEligibilityTracker gameTracker,
            final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker)
    }

    @Override
    boolean targetSelf() {
        return true
    }

    @Override
    void validateMoveSpecific(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        if (game.playerDetails[player.id].ecmsRemaining < 1) {
            throw new NoECMActionsRemainException()
        }

    }

    @Override
    TBGame playMove(
            final MongoPlayer player,
            final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        TBActionLogEntry logEntry = new TBActionLogEntry(
                actionType: TBActionLogEntry.TBActionType.UsedECM,
                description: player.displayName + " deployed an ECM."
        )
        game.playerDetails.each {
            it.value.actionLog.add(logEntry)
        }

        TBPlayerState state = game.playerDetails[player.id]
        --state.ecmsRemaining

        Set<GridCoordinate> ecmCoordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate)

        game.playerDetails.findAll { it.key != player.id }.each {
            ObjectId pid, TBPlayerState opponent ->
                Grid opponentGrid = opponent.opponentGrids[player.id]
                Grid opponentView = state.opponentViews[pid]
                ecmCoordinates.each {
                    opponentGrid.set(it, GridCellState.Unknown)
                    opponentView.set(it, GridCellState.Unknown)
                    String start = "You fired at " + player.displayName + " " + it
                    opponent.actionLog.findAll {
                        it.actionType == TBActionLogEntry.TBActionType.Fired && it.description.startsWith(start)
                    }.each {
                        it.description = "Log damaged by ECM."
                        it.actionType = TBActionLogEntry.TBActionType.DamagedByECM
                    }
                }
        }

        game
    }

}
