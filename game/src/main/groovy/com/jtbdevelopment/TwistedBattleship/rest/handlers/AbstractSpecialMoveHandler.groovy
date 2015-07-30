package com.jtbdevelopment.TwistedBattleship.rest.handlers

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import groovy.transform.CompileStatic

/**
 * Date: 5/19/15
 * Time: 6:38 AM
 */
@CompileStatic
abstract class AbstractSpecialMoveHandler extends AbstractPlayerMoveHandler {
    @Override
    int movesRequired(final TBGame game) {
        return game.movesForSpecials
    }
}
