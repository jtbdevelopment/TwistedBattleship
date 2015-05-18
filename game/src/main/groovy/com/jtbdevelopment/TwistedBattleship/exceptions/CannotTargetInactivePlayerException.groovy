package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/15/15
 * Time: 3:11 PM
 */
class CannotTargetInactivePlayerException extends GameInputException {
    private static final String ERROR = "Can only target active players."

    CannotTargetInactivePlayerException() {
        super(ERROR)
    }
}
