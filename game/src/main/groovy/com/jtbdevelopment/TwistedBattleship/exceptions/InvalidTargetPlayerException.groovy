package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/12/15
 * Time: 7:05 AM
 */
class InvalidTargetPlayerException extends GameInputException {
    private static final String ERROR = "Cannot target this player with this move."

    InvalidTargetPlayerException() {
        super(ERROR)
    }
}
