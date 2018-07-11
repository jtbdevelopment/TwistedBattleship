package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/12/15
 * Time: 7:05 AM
 */
public class InvalidTargetPlayerException extends GameInputException {
    private static final String ERROR = "Cannot target this player with this move.";

    public InvalidTargetPlayerException() {
        super(ERROR);
    }
}
