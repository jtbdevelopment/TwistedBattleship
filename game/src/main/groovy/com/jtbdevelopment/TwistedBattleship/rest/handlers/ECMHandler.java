package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoECMActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * Date: 5/20/15
 * Time: 6:35 PM
 */
@Component
public class ECMHandler extends AbstractSpecialMoveHandler {
    @Autowired
    private GridCircleUtil gridCircleUtil;

    public ECMHandler(final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, final GameTransitionEngine<TBGame> transitionEngine, final GamePublisher<TBGame, MongoPlayer> gamePublisher, final GameEligibilityTracker gameTracker, final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
    }

    @Override
    public boolean targetSelf() {
        return true;
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        if (game.getPlayerDetails().get(player.getId()).getEcmsRemaining() < 1) {
            throw new NoECMActionsRemainException();
        }
    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        TBActionLogEntry logEntry = new TBActionLogEntry();

        logEntry.setActionType(TBActionLogEntry.TBActionType.UsedECM);
        logEntry.setDescription(player.getDisplayName() + " deployed an ECM.");
        game.getPlayerDetails().values().forEach(state -> {
            state.getActionLog().add(logEntry);
        });

        final TBPlayerState state = game.getPlayerDetails().get(player.getId());
        state.setEcmsRemaining(state.getEcmsRemaining() - 1);

        final Set<GridCoordinate> ecmCoordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate);

        game.getPlayerDetails().forEach((pid, opponent) -> {
            if (pid.equals(player.getId())) {
                return;
            }

            final Grid opponentGrid = opponent.getOpponentGrids().get(player.getId());
            final Grid opponentView = state.getOpponentViews().get(pid);
            ecmCoordinates.forEach(ecmCoordinate -> {
                opponentGrid.set(ecmCoordinate, GridCellState.Unknown);
                opponentView.set(ecmCoordinate, GridCellState.Unknown);
                String logStart = "You fired at " + player.getDisplayName() + " " + ecmCoordinate;
                opponent.getActionLog().stream()
                        .filter(log -> log.getDescription().startsWith(logStart))
                        .filter(log -> log.getActionType().equals(TBActionLogEntry.TBActionType.Fired))
                        .forEach(entry -> {
                            entry.setDescription("Log damaged by ECM.");
                            entry.setActionType(TBActionLogEntry.TBActionType.DamagedByECM);

                        });
            });
        });

        return game;
    }
}
