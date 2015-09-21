package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.exceptions.GameIsNotInSetupPhaseException
import com.jtbdevelopment.TwistedBattleship.exceptions.ShipNotInitializedCorrectlyException
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipPlacementValidator
import com.jtbdevelopment.TwistedBattleship.state.ships.ShipState
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import com.jtbdevelopment.games.state.GamePhase
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * Date: 4/30/15
 * Time: 12:26 PM
 */
@Component
@CompileStatic
class SetupShipsHandler extends AbstractGameActionHandler<List<ShipState>, TBGame> {
    @Autowired
    ShipPlacementValidator shipPlacementValidator

    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final List<ShipState> param) {
        validateGame(game);
        validateShipStates(game, param)
        game.playerDetails[(ObjectId) player.id].shipStates = param

        game
    }

    private void validateShipStates(final TBGame game, final List<ShipState> states) {
        states.each {
            ShipState state ->
                if (state.ship == null
                        || state.healthRemaining != state.ship.gridSize
                        || state.shipSegmentHit.size() != state.ship.gridSize
                        || state.shipSegmentHit.contains(true)
                ) {
                    throw new ShipNotInitializedCorrectlyException()
                }
        }
        shipPlacementValidator.validateShipPlacementsForGame(game, states)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    private void validateGame(final TBGame game) {
        if (GamePhase.Setup != game.gamePhase) {
            throw new GameIsNotInSetupPhaseException()
        }
    }
}
