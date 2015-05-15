package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException

/**
 * Date: 5/15/15
 * Time: 7:03 AM
 */
class NoSpyActionsRemainException extends GameInputException {
    private static final String ERROR = "You are out of spy drones."

    NoSpyActionsRemainException() {
        super(ERROR)
    }
}
