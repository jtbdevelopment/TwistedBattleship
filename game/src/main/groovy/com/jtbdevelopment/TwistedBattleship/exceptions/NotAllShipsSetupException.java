package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 4/30/15
 * Time: 8:48 PM
 */
public class NotAllShipsSetupException extends GameInputException {
    private static final String ERROR = "Not all ships have been submitted.";

    public NotAllShipsSetupException() {
        super(ERROR);
    }
}
