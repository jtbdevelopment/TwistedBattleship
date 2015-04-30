package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.grid.GridCoordinate
import com.jtbdevelopment.TwistedBattleship.state.ships.Ship
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 4/30/15
 * Time: 12:26 PM
 */
@Component
@CompileStatic
class SetupShipsHandler extends AbstractGameActionHandler<Map<Ship, List<GridCoordinate>>, TBGame> {
    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final Map<Ship, List<GridCoordinate>> param) {
        return null
    }
}
