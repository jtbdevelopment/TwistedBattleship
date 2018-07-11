package com.jtbdevelopment.TwistedBattleship.factory.gamevalidators;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.factory.GameValidator;
import org.springframework.stereotype.Component;

/**
 * Date: 6/5/15
 * Time: 10:09 PM
 */
@Component
public class PlayerCountValidator implements GameValidator<TBGame> {
    private static final String ERROR_MESSAGE = "A game consists of at least two players and at most six players.";
    private static final int MIN_PLAYERS = 2;
    private static final int MAX_PLAYERS = 6;

    @Override
    public boolean validateGame(final TBGame game) {
        return game.getPlayers().size() >= MIN_PLAYERS && game.getPlayers().size() <= MAX_PLAYERS;
    }

    @Override
    public String errorMessage() {
        return ERROR_MESSAGE;
    }
}
