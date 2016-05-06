package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/5/16
 * Time: 8:45 PM
 */
class NoCruiseMissileActionsRemaining extends GameInputException {
    private static final String ERROR = "You have no more cruise missiles to fire."

    NoCruiseMissileActionsRemaining() {
        super(ERROR)
    }
}
