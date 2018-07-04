package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoEmergencyManeuverActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.FogCoordinatesGenerator;
import com.jtbdevelopment.TwistedBattleship.rest.handlers.helpers.ShipRelocator;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.Grid;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Date: 5/21/15
 * Time: 6:39 AM
 */
@Component
public class EvasiveManeuverHandler extends AbstractSpecialMoveHandler {
    @Autowired
    private ShipRelocator shipRelocator;
    @Autowired
    private FogCoordinatesGenerator fogCoordinatesGenerator;

    public EvasiveManeuverHandler(AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, GameTransitionEngine<TBGame> transitionEngine, GamePublisher<TBGame, MongoPlayer> gamePublisher, GameEligibilityTracker gameTracker, GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
    }

    @Override
    public boolean targetSelf() {
        return true;
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState state = game.getPlayerDetails().get(player.getId());
        if (state.getEvasiveManeuversRemaining() < 1) {
            throw new NoEmergencyManeuverActionsRemainException();
        }

        if (!state.getCoordinateShipMap().containsKey(coordinate)) {
            throw new NoShipAtCoordinateException();
        }
    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.getPlayerDetails().get(player.getId());
        ShipState ship = playerState.getCoordinateShipMap().get(coordinate);

        List<GridCoordinate> newCoordinates = shipRelocator.relocateShip(game, playerState, ship);
        Set<GridCoordinate> fogCoordinates = fogCoordinatesGenerator.generateFogCoordinates(game, ship.getShipGridCells(), newCoordinates);
        ship.setShipGridCells(newCoordinates);
        playerState.getCoordinateShipMap().clear();
        TBActionLogEntry entry = new TBActionLogEntry(
                player.getDisplayName() + " performed evasive maneuvers.",
                TBActionLogEntry.TBActionType.PerformedManeuvers);

        playerState.getActionLog().add(entry);
        fogGrids(game, player, playerState, entry, fogCoordinates);
        playerState.setEvasiveManeuversRemaining(playerState.getEvasiveManeuversRemaining() - 1);
        return game;
    }

    private void fogGrids(final TBGame game, final Player<ObjectId> player, final TBPlayerState playerState, final TBActionLogEntry actionLogEntry, final Set<GridCoordinate> fogCoordinates) {
        game.getPlayerDetails().entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(player.getId()))
                .forEach(e -> {
                    ObjectId opponent = e.getKey();
                    TBPlayerState opponentState = e.getValue();
                    opponentState.getActionLog().add(actionLogEntry);
                    final Grid opponentGrid = opponentState.getOpponentGrids().get(player.getId());
                    final Grid opponentView = playerState.getOpponentViews().get(opponent);
                    fogCoordinates.forEach(fog -> {
                        switch (opponentGrid.get(fog)) {
                            case KnownShip:
                                opponentGrid.set(fog, GridCellState.ObscuredShip);
                                opponentView.set(fog, GridCellState.ObscuredShip);
                                break;
                            case KnownByHit:
                                opponentGrid.set(fog, GridCellState.ObscuredHit);
                                opponentView.set(fog, GridCellState.ObscuredHit);
                                break;
                            case KnownByOtherHit:
                                opponentGrid.set(fog, GridCellState.ObscuredOtherHit);
                                opponentView.set(fog, GridCellState.ObscuredOtherHit);
                                break;
                            case KnownByRehit:
                                opponentGrid.set(fog, GridCellState.ObscuredRehit);
                                opponentView.set(fog, GridCellState.ObscuredRehit);
                                break;
                            case KnownByMiss:
                                opponentGrid.set(fog, GridCellState.ObscuredMiss);
                                opponentView.set(fog, GridCellState.ObscuredMiss);
                                break;
                            case KnownByOtherMiss:
                                opponentGrid.set(fog, GridCellState.ObscuredOtherMiss);
                                opponentView.set(fog, GridCellState.ObscuredOtherMiss);
                                break;
                            case KnownEmpty:
                                opponentGrid.set(fog, GridCellState.ObscuredEmpty);
                                opponentView.set(fog, GridCellState.ObscuredEmpty);
                                break;
                        }
                    });
                });
    }

    public void setShipRelocator(ShipRelocator shipRelocator) {
        this.shipRelocator = shipRelocator;
    }

    public void setFogCoordinatesGenerator(FogCoordinatesGenerator fogCoordinatesGenerator) {
        this.fogCoordinatesGenerator = fogCoordinatesGenerator;
    }
}
