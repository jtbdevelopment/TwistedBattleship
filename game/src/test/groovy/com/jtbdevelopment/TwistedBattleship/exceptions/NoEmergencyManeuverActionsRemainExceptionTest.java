package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/20/15
 * Time: 7:16 PM
 */
public class NoEmergencyManeuverActionsRemainExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("The ships cannot handler any further emergency maneuvers.", new NoEmergencyManeuverActionsRemainException().getMessage());
    }

}
