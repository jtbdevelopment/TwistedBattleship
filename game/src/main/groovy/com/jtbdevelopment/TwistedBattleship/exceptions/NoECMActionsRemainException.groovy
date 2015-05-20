package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/20/15
 * Time: 6:37 PM
 */
class NoECMActionsRemainException extends GameInputException {
    private static final String ERROR = "You have no more ECM devices to deploy."

    NoECMActionsRemainException() {
        super(ERROR)
    }
}
