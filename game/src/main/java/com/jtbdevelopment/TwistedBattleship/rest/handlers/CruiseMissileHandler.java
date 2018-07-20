package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.NoCruiseMissileActionsRemaining;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBActionLogEntry;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState;
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
import org.springframework.stereotype.Component;

/**
 * Date: 5/5/16
 * Time: 8:43 PM
 */
@Component
public class CruiseMissileHandler extends AbstractSpecialMoveHandler {
    private final FireAtCoordinateHandler fireAtCoordinateHandler;

    CruiseMissileHandler(
            final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository,
            final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository,
            final GameTransitionEngine<TBGame> transitionEngine,
            final GamePublisher<TBGame, MongoPlayer> gamePublisher,
            final GameEligibilityTracker gameTracker,
            final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker,
            final FireAtCoordinateHandler fireAtCoordinateHandler) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
        this.fireAtCoordinateHandler = fireAtCoordinateHandler;
    }

    @Override
    public boolean targetSelf() {
        return false;
    }

    @Override
    public TBGame playMove(final MongoPlayer player, final TBGame game, final MongoPlayer targetedPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.getPlayerDetails().get(player.getId());
        TBPlayerState targetState = game.getPlayerDetails().get(targetedPlayer.getId());
        ShipState shipState = targetState.getCoordinateShipMap().get(coordinate);

        generateLogEntries(game, player, targetedPlayer, coordinate, shipState);

        playerState.setCruiseMissilesRemaining(playerState.getCruiseMissilesRemaining() - 1);
        if (shipState != null) {
            shipState.getShipGridCells().forEach(shipCoordinate -> fireAtCoordinateHandler.playMove(player, game, targetedPlayer, shipCoordinate));
        } else {
            fireAtCoordinateHandler.playMove(player, game, targetedPlayer, coordinate);
        }

        return game;
    }

    private void generateLogEntries(TBGame game, Player<ObjectId> player, Player<ObjectId> targetedPlayer, GridCoordinate coordinate, ShipState shipState) {
        boolean sharedState = game.getFeatures().contains(GameFeature.SharedIntel);
        String playerMessage = "You fired a cruise missile at " + targetedPlayer.getDisplayName() + " " + coordinate + " and ";
        String targetMessage = player.getDisplayName() + " fired a cruise missile at " + coordinate + " and ";
        String otherMessage = player.getDisplayName() + " fired a cruise missile at " + targetedPlayer.getDisplayName();
        if (sharedState) otherMessage += " " + coordinate + " and ";
        if (shipState != null) {
            playerMessage += "hit!";
            targetMessage += "hit!";
            if (sharedState) otherMessage += "hit!";
            else otherMessage += ".";
        } else {
            playerMessage += "missed.";
            targetMessage += "missed.";
            if (sharedState) otherMessage += "missed.";
            else otherMessage += ".";
        }

        addMessages(game, player, playerMessage, targetedPlayer, targetMessage, otherMessage);
    }

    private void addMessages(TBGame game, final Player<ObjectId> player, final String playerMessage, final Player<ObjectId> targetedPlayer, final String targetMessage, final String otherMessage) {
        game.getPlayerDetails().forEach((playerId, state) -> {
            if (playerId.equals(player.getId())) {
                state.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.CruiseMissile, playerMessage));

            } else if (playerId.equals(targetedPlayer.getId())) {
                state.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.CruiseMissile, targetMessage));

            } else {
                state.getActionLog().add(new TBActionLogEntry(TBActionLogEntry.TBActionType.CruiseMissile, otherMessage));

            }
        });
    }

    @Override
    public void validateMoveSpecific(final MongoPlayer player, final TBGame game, final MongoPlayer targetPlayer, final GridCoordinate coordinate) {
        TBPlayerState playerState = game.getPlayerDetails().get(player.getId());

        if (playerState.getCruiseMissilesRemaining() < 1) {
            throw new NoCruiseMissileActionsRemaining();

        }


        //  Will not prevent you from firing on empty spaces if you want to waste it
    }
}
