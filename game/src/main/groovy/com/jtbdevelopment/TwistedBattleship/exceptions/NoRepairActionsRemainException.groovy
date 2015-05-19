package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/19/15
 * Time: 6:48 AM
 */
class NoRepairActionsRemainException extends GameInputException {
    private static final String ERROR = "You are no out of emergency repair materials."

    NoRepairActionsRemainException() {
        super(ERROR)
    }
}
