package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/19/15
 * Time: 6:43 AM
 */
class CannotRepairADestroyedShipException extends GameInputException {
    private static final String ERROR = "You cannot repair a completely destroyed ship."

    CannotRepairADestroyedShipException() {
        super(ERROR)
    }
}
