package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/5/15
 * Time: 6:46 AM
 */
public class GameIsNotInSetupPhaseException extends GameInputException {
    private static final String ERROR = "Ships cannot be placed outside of setup phase.";

    public GameIsNotInSetupPhaseException() {
        super(ERROR);
    }
}
