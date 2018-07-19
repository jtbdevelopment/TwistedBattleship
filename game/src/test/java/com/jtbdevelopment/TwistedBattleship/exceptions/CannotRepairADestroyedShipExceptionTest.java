package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 9/18/15
 * Time: 6:32 PM
 */
public class CannotRepairADestroyedShipExceptionTest {
    @Test
    public void testMessage() {
        assertEquals("You cannot repair a completely destroyed ship.", new CannotRepairADestroyedShipException().getMessage());
    }

}
