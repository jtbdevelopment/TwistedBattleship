package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/8/15
 * Time: 6:50 PM
 */
public class CoordinateOutOfBoundsException extends GameInputException {
    private static String ERROR = "Coordinate is not inside game boundaries.";

    public CoordinateOutOfBoundsException() {
        super(ERROR);
    }
}
