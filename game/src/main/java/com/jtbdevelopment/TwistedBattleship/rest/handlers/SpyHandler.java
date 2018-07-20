package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoSpyActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCircleUtil;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Date: 5/15/15
 * Time: 6:55 AM
 */
@Component
public class SpyHandler extends AbstractSpecialMoveHandler {
    private final GridCircleUtil gridCircleUtil;

    SpyHandler(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
            final GameTransitionEngine<TBGame> transitionEngine,
            final GamePublisher<TBGame, MongoPlayer> gamePublisher,
            final GameEligibilityTracker gameTracker, GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker,
            final GridCircleUtil gridCircleUtil) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
        this.gridCircleUtil = gridCircleUtil;
    }

    @Override
    public boolean targetSelf() {
        return false;
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        if (game.getPlayerDetails().get(player.getId()).getSpysRemaining() < 1) {
            throw new NoSpyActionsRemainException();
        }

    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        Set<GridCoordinate> coordinates = gridCircleUtil.computeCircleCoordinates(game, coordinate);
        Map<GridCoordinate, GridCellState> spyResults = computeSpyCoordinateStates(game, targetedPlayer, coordinates);
        updatePlayerGrids(game, player, targetedPlayer, spyResults, coordinate);
        game.getPlayerDetails().get(player.getId()).setSpysRemaining(game.getPlayerDetails().get(player.getId()).getSpysRemaining() - 1);
        return game;
    }

    private void updatePlayerGrids(final TBGame game, final MongoPlayer player, final MongoPlayer targetedPlayer, final Map<GridCoordinate, GridCellState> spyResults, final GridCoordinate targetCoordinate) {
        game.getPlayerDetails().get(player.getId()).getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Spied, "You spied on " + targetedPlayer.getDisplayName() + " at " + targetCoordinate + "."));
        game.getPlayerDetails().get(targetedPlayer.getId()).getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Spied, player.getDisplayName() + " spied on you at " + targetCoordinate + "."));
        final boolean sharedIntel = game.getFeatures().contains(GameFeature.SharedIntel);
        final String messageForOthers = player.getDisplayName() + " spied on " + targetedPlayer.getDisplayName() + " at " + targetCoordinate + ".";

        game.getPlayerDetails().entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(targetedPlayer.getId()))
                .filter(e -> sharedIntel || e.getKey().equals(player.getId()))
                .forEach((entry) -> {
                    ObjectId id = entry.getKey();
                    TBPlayerState state = entry.getValue();
                    Grid playerGrid = state.getOpponentGrids().get(targetedPlayer.getId());
                    Grid targetView = game.getPlayerDetails().get(targetedPlayer.getId()).getOpponentViews().get(id);
                    updateGridForPlayer(playerGrid, targetView, spyResults);
                    if (!id.equals(player.getId())) {
                        state.getActionLog().add(0, new TBActionLogEntry(TBActionLogEntry.TBActionType.Spied, messageForOthers));
                    }
                });
    }

    private void updateGridForPlayer(final Grid playerGrid, final Grid targetView, final Map<GridCoordinate, GridCellState> spyResults) {
        spyResults.forEach((coordinate, state) -> {
            if (playerGrid.get(coordinate).getRank() < state.getRank()) {
                playerGrid.set(coordinate, state);
                targetView.set(coordinate, state);
            }
        });
    }

    private Map<GridCoordinate, GridCellState> computeSpyCoordinateStates(final TBGame game, final MongoPlayer targetedPlayer, final Set<GridCoordinate> coordinates) {
        final TBPlayerState targetedState = game.getPlayerDetails().get(targetedPlayer.getId());
        return coordinates.stream()
                .collect(Collectors.toMap(
                        coordinate -> coordinate,
                        coordinate -> {
                            ShipState shipState = targetedState.getCoordinateShipMap().get(coordinate);
                            if (shipState == null) {
                                return GridCellState.KnownEmpty;
                            }

                            return shipState.getShipSegmentHit().get(shipState.getShipGridCells().indexOf(coordinate))
                                    ? GridCellState.KnownByOtherHit : GridCellState.KnownShip;
                        }));
    }
}
