package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.CannotTargetInactivePlayerException;
import com.jtbdevelopment.TwistedBattleship.exceptions.CoordinateOutOfBoundsException;
import com.jtbdevelopment.TwistedBattleship.exceptions.InvalidTargetPlayerException;
import com.jtbdevelopment.TwistedBattleship.exceptions.NotEnoughActionsForSpecialException;
import com.jtbdevelopment.TwistedBattleship.rest.Target;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.exceptions.input.GameIsNotInPlayModeException;
import com.jtbdevelopment.games.exceptions.input.PlayerOutOfTurnException;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.players.Player;
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;

import java.util.stream.Collectors;

/**
 * Date: 5/7/15
 * Time: 8:21 PM
 */
public abstract class AbstractPlayerMoveHandler extends AbstractGameActionHandler<Target, ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {
    public AbstractPlayerMoveHandler(final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, final GameTransitionEngine<TBGame> transitionEngine, final GamePublisher<TBGame, MongoPlayer> gamePublisher, final GameEligibilityTracker gameTracker, final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
    }

    public abstract boolean targetSelf();

    public abstract int movesRequired(final TBGame game);

    public abstract TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate);

    public abstract void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate);

    @Override
    protected TBGame handleActionInternal(final MongoPlayer player, final TBGame game, final Target target) {

        Player targetPlayer = loadPlayerMD5(target.getPlayer());
        validateMove(player, game, (MongoPlayer) targetPlayer, target.getCoordinate());
        return markHiddenHits(playMove(player, game, (MongoPlayer) targetPlayer, target.getCoordinate()), player);
    }

    private TBGame markHiddenHits(TBGame game, MongoPlayer player) {
        final TBPlayerState state = game.getPlayerDetails().get(player.getId());
        state.getShipStates().forEach(shipState -> {
            for (int shipCell = 0; shipCell < shipState.getShip().getGridSize(); ++shipCell) {
                if (shipState.getShipSegmentHit().get(shipCell)) {
                    int finalShipCell = shipCell;
                    state.getOpponentViews().forEach((opponent, opponentView) -> {
                        GridCoordinate shipCellCoordinate = shipState.getShipGridCells().get(finalShipCell);
                        if (opponentView.get(shipCellCoordinate).getRank() < GridCellState.HiddenHit.getRank()) {
                            opponentView.set(shipCellCoordinate, GridCellState.HiddenHit);
                        }
                    });
                }
            }
        });
        return game;
    }

    @Override
    protected TBGame rotateTurnBasedGame(final TBGame game) {
        game.setRemainingMoves(game.getRemainingMoves() - movesRequired(game));
        if (game.getRemainingMoves() == 0) {
            if (game.getPlayerDetails().values().stream().filter(TBPlayerState::isAlive).count() > 1) {
                int newIndex = game.getPlayers()
                        .stream()
                        .map(Player::getId)
                        .collect(Collectors.toList())
                        .indexOf(game.getCurrentPlayer());
                boolean found = false;
                while (!found) {
                    newIndex++;
                    if (newIndex == game.getPlayers().size()) {
                        newIndex = 0;
                    }
                    ObjectId playerId = game.getPlayers().get(newIndex).getId();
                    TBPlayerState tbPlayerState = game.getPlayerDetails().get(playerId);
                    if (tbPlayerState.isAlive()) {
                        game.setCurrentPlayer(playerId);
                        if (game.getFeatures().contains(GameFeature.PerShip)) {
                            game.setRemainingMoves(tbPlayerState.getActiveShipsRemaining());
                        } else {
                            game.setRemainingMoves(1);
                        }
                        found = true;
                    }
                }

            }
        }
        return game;
    }

    private void validateMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        if (!game.getCurrentPlayer().equals(player.getId())) {
            throw new PlayerOutOfTurnException();
        }


        validatePlayerForGame(game, targetPlayer);

        if (targetSelf() != (player.equals(targetPlayer))) {
            throw new InvalidTargetPlayerException();
        }


        if (game.getRemainingMoves() < movesRequired(game)) {
            throw new NotEnoughActionsForSpecialException();
        }


        if (!coordinate.isValidCoordinate(game)) {
            throw new CoordinateOutOfBoundsException();
        }


        if (!game.getGamePhase().equals(GamePhase.Playing)) {
            throw new GameIsNotInPlayModeException();
        }


        if (!game.getPlayerDetails().get(targetPlayer.getId()).isAlive()) {
            throw new CannotTargetInactivePlayerException();
        }

        validateMoveSpecific(player, game, targetPlayer, coordinate);
    }

}
