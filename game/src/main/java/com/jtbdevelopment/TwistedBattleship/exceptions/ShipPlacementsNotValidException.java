package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 4/30/15
 * Time: 8:48 PM
 */
public class ShipPlacementsNotValidException extends GameInputException {
    private static final String ERROR = "Ship(s) are not placed validly.";

    public ShipPlacementsNotValidException() {
        super(ERROR);
    }
}
