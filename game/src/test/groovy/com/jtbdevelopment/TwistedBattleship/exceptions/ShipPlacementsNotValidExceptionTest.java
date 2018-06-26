package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/1/15
 * Time: 6:51 AM
 */
public class ShipPlacementsNotValidExceptionTest {
    @Test
    public void testErrorMessage() {
        assertEquals("Ship(s) are not placed validly.", new ShipPlacementsNotValidException().getMessage());
    }

}
