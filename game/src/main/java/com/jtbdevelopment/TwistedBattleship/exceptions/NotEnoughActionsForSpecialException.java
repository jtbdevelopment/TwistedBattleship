package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/8/15
 * Time: 7:06 AM
 */
public class NotEnoughActionsForSpecialException extends GameInputException {
    private static final String ERROR = "Not enough actions remain to use a special.";

    public NotEnoughActionsForSpecialException() {
        super(ERROR);
    }
}
