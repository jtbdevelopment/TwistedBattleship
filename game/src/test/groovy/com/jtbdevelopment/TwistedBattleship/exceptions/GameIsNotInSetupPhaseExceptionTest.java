package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Date: 5/5/15
 * Time: 6:46 AM
 */
public class GameIsNotInSetupPhaseExceptionTest {
    @Test
    public void testErrorMessage() {
        assertEquals("Ships cannot be placed outside of setup phase.", new GameIsNotInSetupPhaseException().getMessage());
    }

}
