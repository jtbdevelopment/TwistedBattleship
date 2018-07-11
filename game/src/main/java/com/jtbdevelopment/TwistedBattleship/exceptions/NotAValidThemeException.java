package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 8/20/15
 * Time: 7:00 AM
 */
public class NotAValidThemeException extends GameInputException {
    private static final String ERROR = "This is not a valid theme choice.";

    public NotAValidThemeException() {
        super(ERROR);
    }
}
