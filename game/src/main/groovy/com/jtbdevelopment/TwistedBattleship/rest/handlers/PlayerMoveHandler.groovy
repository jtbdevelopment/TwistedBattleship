package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.rest.GameActionInfo
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.rest.handlers.AbstractGameActionHandler
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 5/7/15
 * Time: 8:21 PM
 */
@Component
@CompileStatic
class PlayerMoveHandler extends AbstractGameActionHandler<GameActionInfo, TBGame> {
    @Override
    protected TBGame handleActionInternal(
            final Player player, final TBGame game, final GameActionInfo param) {
        return null
    }
}
