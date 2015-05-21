package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/19/15
 * Time: 6:43 AM
 */
class NoShipAtCoordinateException extends GameInputException {
    private static final String ERROR = "There is no ship at that coordinate."

    NoShipAtCoordinateException() {
        super(ERROR)
    }
}
