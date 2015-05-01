package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException
import groovy.transform.CompileStatic

/**
 * Date: 4/30/15
 * Time: 8:48 PM
 */
@CompileStatic
class ShipPlacementsNotValidException extends GameInputException {
    private final static String ERROR = "Ship(s) are placed validly."

    ShipPlacementsNotValidException() {
        super(ERROR)
    }
}
