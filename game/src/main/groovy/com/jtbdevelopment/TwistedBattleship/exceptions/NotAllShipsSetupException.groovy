package com.jtbdevelopment.TwistedBattleship.exceptions

import com.jtbdevelopment.games.exceptions.GameInputException
import groovy.transform.CompileStatic

/**
 * Date: 4/30/15
 * Time: 8:48 PM
 */
@CompileStatic
class NotAllShipsSetupException extends GameInputException {
    private final static String ERROR = "Not all ships have been submitted."

    NotAllShipsSetupException() {
        super(ERROR)
    }
}
