package com.jtbdevelopment.TwistedBattleship.rest.handlers;

import com.jtbdevelopment.TwistedBattleship.exceptions.GameIsNotInSetupPhaseException;
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipNotInitializedCorrectlyException;
import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.TwistedBattleship.state.masked.TBMaskedGame;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator;
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState;
import com.jtbdevelopment.games.dao.AbstractGameRepository;
import com.jtbdevelopment.games.dao.AbstractPlayerRepository;
import com.jtbdevelopment.games.events.GamePublisher;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler;
import com.jtbdevelopment.games.state.GamePhase;
import com.jtbdevelopment.games.state.masking.GameMasker;
import com.jtbdevelopment.games.state.transition.GameTransitionEngine;
import com.jtbdevelopment.games.tracking.GameEligibilityTracker;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Date: 4/30/15
 * Time: 12:26 PM
 */
@Component
public class SetupShipsHandler extends AbstractGameActionHandler<List<ShipState>, ObjectId, GameFeature, TBGame, TBMaskedGame, MongoPlayer> {
    private final ShipPlacementValidator shipPlacementValidator;

    public SetupShipsHandler(final ShipPlacementValidator shipPlacementValidator, final AbstractPlayerRepository<ObjectId, MongoPlayer> playerRepository, final AbstractGameRepository<ObjectId, GameFeature, TBGame> gameRepository, final GameTransitionEngine<TBGame> transitionEngine, final GamePublisher<TBGame, MongoPlayer> gamePublisher, final GameEligibilityTracker gameTracker, final GameMasker<ObjectId, TBGame, TBMaskedGame> gameMasker) {
        super(playerRepository, gameRepository, transitionEngine, gamePublisher, gameTracker, gameMasker);
        this.shipPlacementValidator = shipPlacementValidator;
    }

    @Override
    protected TBGame handleActionInternal(final MongoPlayer player, final TBGame game, final List<ShipState> param) {
        validateGame(game);
        validateShipStates(game, param);
        game.getPlayerDetails().get(player.getId()).setShipStates(param);
        game.getPlayerDetails().get(player.getId()).setSetup(true);

        return game;
    }

    private void validateShipStates(final TBGame game, final List<ShipState> states) {
        states.forEach(state -> {
            if (state.getShip() == null || state.getHealthRemaining() != state.getShip().getGridSize() || state.getShipSegmentHit().size() != state.getShip().getGridSize() || state.getShipSegmentHit().contains(true)) {
                throw new ShipNotInitializedCorrectlyException();
            }
        });
        shipPlacementValidator.validateShipPlacementsForGame(game, states);
    }

    private void validateGame(final TBGame game) {
        if (!GamePhase.Setup.equals(game.getGamePhase())) {
            throw new GameIsNotInSetupPhaseException();
        }

    }
}
