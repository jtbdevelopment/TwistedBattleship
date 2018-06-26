package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/8/15
 * Time: 7:07 AM
 */
public class NotEnoughActionsForSpecialExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("Not enough actions remain to use a special.", new NotEnoughActionsForSpecialException().getMessage());
    }

}
