package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCellState;
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.scoring.TBGameScorer;
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
import org.springframework.stereotype.Component;

/**
 * Date: 5/11/15
 * Time: 7:08 PM
 */
@Component
public class FireAtCoordinateHandler extends AbstractPlayerMoveHandler {
    public FireAtCoordinateHandler(AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, GameTransitionEngine<TBGame> transitionEngine, GamePublisher<TBGame, MongoPlayer> gamePublisher, GameEligibilityTracker gameTracker, GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
    }

    @Override
    public boolean targetSelf() {
        return false;
    }

    @Override
    public int movesRequired(final TBGame game) {
        return 1;
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        //
    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState targetedState = game.getPlayerDetails().get(targetedPlayer.getId());
        TBPlayerState playerState = game.getPlayerDetails().get(player.getId());
        ShipState ship = targetedState.getCoordinateShipMap().get(coordinate);
        if (ship != null) {
            int hitIndex = ship.getShipGridCells().indexOf(coordinate);
            if (ship.getShipSegmentHit().get(hitIndex)) {
                processReHit(game, player, playerState, targetedPlayer, targetedState, coordinate, ship);
            } else {
                processHit(game, player, playerState, targetedPlayer, targetedState, coordinate, ship, hitIndex);
            }

        } else {
            processMiss(game, player, playerState, targetedPlayer, targetedState, coordinate);
        }

        return game;
    }

    private void processMiss(final TBGame game, final MongoPlayer player, final TBPlayerState playerState, final MongoPlayer targetedPlayer, final TBPlayerState targetedState, final GridCoordinate coordinate) {
        playerState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, "You fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and missed."));
        targetedState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, player.getDisplayName() + " fired at " + coordinate + " and missed."));
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(player.getDisplayName() + " fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and missed.", TBActionLogEntry.TBActionType.Fired);
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByMiss, GridCellState.KnownByOtherMiss, sharedLogEntry);
    }

    private void processHit(final TBGame game, final Player<ObjectId> player, final TBPlayerState playerState, final Player<ObjectId> targetedPlayer, final TBPlayerState targetedState, final GridCoordinate coordinate, final ShipState ship, final int hitIndex) {
        ship.getShipSegmentHit().set(hitIndex, true);
        ship.setHealthRemaining(ship.getHealthRemaining() - 1);
        playerState.setScoreFromHits(playerState.getScoreFromHits() + TBGameScorer.SCORE_FOR_HIT);
        playerState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, "You fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and hit!"));
        targetedState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, player.getDisplayName() + " fired at " + coordinate + " and hit your " + ship.getShip().getDescription() + "!"));
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(player.getDisplayName() + " fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and hit!", TBActionLogEntry.TBActionType.Fired);
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByHit, GridCellState.KnownByOtherHit, sharedLogEntry);
        checkForShipSinking(game, player, playerState, targetedPlayer, targetedState, ship);
    }

    private void checkForShipSinking(final TBGame game, final Player<ObjectId> player, final TBPlayerState playerState, final Player<ObjectId> targetedPlayer, final TBPlayerState targetedState, final ShipState ship) {
        if (ship.getHealthRemaining() == 0) {
            targetedState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Sunk, player.getDisplayName() + " sunk your " + ship.getShip().getDescription() + "!"));
            playerState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Sunk, "You sunk a " + ship.getShip().getDescription() + " for " + targetedPlayer.getDisplayName() + "!"));
            if (game.getFeatures().contains(GameFeature.SharedIntel)) {
                final TBActionLogEntry sharedLogEntry = new TBActionLogEntry(player.getDisplayName() + " sunk a " + ship.getShip().getDescription() + " for " + targetedPlayer.getDisplayName() + "!", TBActionLogEntry.TBActionType.Sunk);
                game.getPlayerDetails().forEach((id, details) -> {
                    if (id.equals(player.getId()) || id.equals(targetedPlayer.getId())) {
                        return;
                    }
                    details.getActionLog().add(sharedLogEntry);
                });
            }

            playerState.setScoreFromSinks(playerState.getScoreFromSinks() + TBGameScorer.SCORE_FOR_SINK);
            if (!targetedState.isAlive()) {
                final TBActionLogEntry logEntry = new TBActionLogEntry(TBActionLogEntry.TBActionType.Defeated, targetedPlayer.getDisplayName() + " has been defeated!");
                game.getPlayerDetails().forEach((id, state) -> state.getActionLog().add(logEntry));
            }

        }

    }

    private void processReHit(final TBGame game, final Player<ObjectId> player, final TBPlayerState playerState, final Player<ObjectId> targetedPlayer, final TBPlayerState targetedState, final GridCoordinate coordinate, final ShipState ship) {
        playerState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, "You fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and hit an already damaged area!"));
        targetedState.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.Fired, player.getDisplayName() + " fired at " + coordinate + " and re-hit your " + ship.getShip().getDescription() + "!"));
        TBActionLogEntry sharedLogEntry = new TBActionLogEntry(player.getDisplayName() + " fired at " + targetedPlayer.getDisplayName() + " " + coordinate + " and re-hit a damaged area!", TBActionLogEntry.TBActionType.Fired);
        markGrids(game, player, targetedPlayer, targetedState, coordinate, GridCellState.KnownByRehit, GridCellState.KnownByOtherHit, sharedLogEntry);
    }

    private void markGrids(final TBGame game, final Player<ObjectId> player, final Player<ObjectId> targetedPlayer, final TBPlayerState targetedState, final GridCoordinate coordinate, final GridCellState markForPlayer, final GridCellState markForOthers, final TBActionLogEntry sharedLogEntry) {
        final boolean sharedState = game.getFeatures().contains(GameFeature.SharedIntel);
        final TBActionLogEntry nonSharedLogEntry = new TBActionLogEntry(player.getDisplayName() + " fired at " + targetedPlayer.getDisplayName() + ".", TBActionLogEntry.TBActionType.Fired);
        game.getPlayerDetails().forEach((id, details) -> {
            if (id.equals(player.getId())) {
                details.getOpponentGrids().get(targetedPlayer.getId()).set(coordinate, markForPlayer);
            } else if (targetedPlayer.getId().equals(id)) {
                details.getOpponentViews().get(player.getId()).set(coordinate, markForPlayer);
            } else {
                if (sharedState) {
                    if (details.getOpponentGrids().get(targetedPlayer.getId()).get(coordinate).getRank() < markForOthers.getRank()) {
                        details.getOpponentGrids().get(targetedPlayer.getId()).set(coordinate, markForOthers);
                        targetedState.getOpponentViews().get(id).set(coordinate, markForOthers);
                    }

                    details.getActionLog().add(sharedLogEntry);
                } else {
                    details.getActionLog().add(nonSharedLogEntry);
                }
            }
        });
    }

}
