package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 4/30/15
 * Time: 8:48 PM
 */
public class ShipNotInitializedCorrectlyException extends GameInputException {
    private static final String ERROR = "Ship(s) are not in a good starting state.";

    public ShipNotInitializedCorrectlyException() {
        super(ERROR);
    }
}
