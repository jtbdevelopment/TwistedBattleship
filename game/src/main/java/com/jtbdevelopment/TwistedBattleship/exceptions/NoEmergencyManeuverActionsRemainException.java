package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/20/15
 * Time: 7:15 PM
 */
public class NoEmergencyManeuverActionsRemainException extends GameInputException {
    private static final String ERROR = "The ships cannot handler any further emergency maneuvers.";

    public NoEmergencyManeuverActionsRemainException() {
        super(ERROR);
    }
}
