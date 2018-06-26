package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/22/15
 * Time: 7:01 PM
 */
public class NoRoomForEmergencyManeuverException extends GameInputException {
    private static final String ERROR = "There is no room for emergency maneuvers for this ship.";

    public NoRoomForEmergencyManeuverException() {
        super(ERROR);
    }
}
