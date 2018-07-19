package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/22/15
 * Time: 7:02 PM
 */
public class NoRoomForEmergencyManeuverExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("There is no room for emergency maneuvers for this ship.", new NoRoomForEmergencyManeuverException().getMessage());
    }

}
