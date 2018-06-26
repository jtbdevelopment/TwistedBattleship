package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/15/15
 * Time: 7:04 AM
 */
public class NoSpyActionsRemainExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("You are out of spy drones.", new NoSpyActionsRemainException().getMessage());
    }

}
