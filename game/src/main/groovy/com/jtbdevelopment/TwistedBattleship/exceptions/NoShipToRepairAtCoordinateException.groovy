package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/19/15
 * Time: 6:43 AM
 */
class NoShipToRepairAtCoordinateException extends GameInputException {
    private static final String ERROR = "There is no ship to repair at that coordinate."

    NoShipToRepairAtCoordinateException() {
        super(ERROR)
    }
}
