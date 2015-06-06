package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.factory.GameValidator
import groovy.transform.CompileStatic
import org.springframework.stereotype.Component

/**
 * Date: 6/5/15
 * Time: 10:09 PM
 */
@CompileStatic
@Component
class PlayerCountValidator implements GameValidator<TBGame> {
    private final static String ERROR_MESSAGE = "A game consists of at least two players and at most six players."
    private final static int MIN_PLAYERS = 2
    private final static int MAX_PLAYERS = 6

    @Override
    boolean validateGame(final TBGame game) {
        return game.players.size() >= MIN_PLAYERS && game.players.size() <= MAX_PLAYERS
    }

    @Override
    String errorMessage() {
        return ERROR_MESSAGE
    }
}

