package com.jtbdevelopment.TwistedBattleship.exceptions;

import com.jtbdevelopment.games.exceptions.GameInputException;

/**
 * Date: 5/15/15
 * Time: 7:03 AM
 */
public class NoSpyActionsRemainException extends GameInputException {
    private static final String ERROR = "You are out of spy drones.";

    public NoSpyActionsRemainException() {
        super(ERROR);
    }
}
