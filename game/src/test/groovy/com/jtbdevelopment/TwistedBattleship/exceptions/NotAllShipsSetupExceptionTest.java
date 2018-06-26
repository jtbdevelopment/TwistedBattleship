package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/1/15
 * Time: 6:35 AM
 */
public class NotAllShipsSetupExceptionTest {
    @Test
    public void testError() {
        NotAllShipsSetupException exception = new NotAllShipsSetupException();
        assertEquals("Not all ships have been submitted.", exception.getMessage());
    }

}
