package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotRepairADestroyedShipException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoRepairActionsRemainException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NoShipAtCoordinateException;
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
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 5/19/15
 * Time: 6:36 AM
 */
@Component
public class RepairShipHandler extends AbstractSpecialMoveHandler {
    public RepairShipHandler(AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, GameTransitionEngine<TBGame> transitionEngine, GamePublisher<TBGame, MongoPlayer> gamePublisher, GameEligibilityTracker gameTracker, GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
    }

    @Override
    public boolean targetSelf() {
        return true;
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.getPlayerDetails().get(player.getId());

        if (playerState.getEmergencyRepairsRemaining() < 1) {
            throw new NoRepairActionsRemainException();
        }


        //  Will let you repair an undamaged ship
        if (!playerState.getCoordinateShipMap().containsKey(coordinate)) {
            throw new NoShipAtCoordinateException();
        }


        if (playerState.getCoordinateShipMap().get(coordinate).getHealthRemaining() == 0) {
            throw new CannotRepairADestroyedShipException();
        }

    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        final TBPlayerState playerState = game.getPlayerDetails().get(player.getId());
        final ShipState state = playerState.getCoordinateShipMap().get(coordinate);

        String message = player.getDisplayName() + " repaired their " + state.getShip().getDescription() + ".";

        final TBActionLogEntry actionLogEntry = new TBActionLogEntry(TBActionLogEntry.TBActionType.Repaired, message);
        playerState.getActionLog().add(actionLogEntry);

        playerState.setEmergencyRepairsRemaining(playerState.getEmergencyRepairsRemaining() - 1);

        game.getPlayerDetails()
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().equals(player.getId()))
                .forEach(e -> {
                    ObjectId id = e.getKey();
                    TBPlayerState opponent = e.getValue();
                    final Grid opponentGrid = opponent.getOpponentGrids().get(player.getId());
                    final Grid opponentView = playerState.getOpponentViews().get(id);
                    state.getShipGridCells().forEach((shipCoordinate) -> {
                        switch (opponentGrid.get(shipCoordinate)) {
                            case KnownByHit:
                            case KnownByRehit:
                            case KnownByOtherHit:
                                opponentGrid.set(shipCoordinate, GridCellState.KnownShip);
                                opponentView.set(shipCoordinate, GridCellState.KnownShip);
                                break;
                        }
                        if (opponentView.get(shipCoordinate).equals(GridCellState.HiddenHit)) {
                            opponentView.set(shipCoordinate, GridCellState.Unknown);
                        }
                    });
                    opponent.getActionLog().add(actionLogEntry);
                });
        state.setHealthRemaining(state.getShip().getGridSize());
        for (int i = 0; i < state.getShipSegmentHit().size(); ++i) {
            state.getShipSegmentHit().set(i, false);
        }
        return game;
    }

}
