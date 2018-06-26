package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/19/15
 * Time: 6:50 AM
 */
public class NoRepairActionsRemainExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("You are no out of emergency repair materials.", new NoRepairActionsRemainException().getMessage());
    }

}
