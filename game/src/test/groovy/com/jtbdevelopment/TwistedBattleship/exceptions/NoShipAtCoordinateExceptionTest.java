package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 5/19/15
 * Time: 6:44 AM
 */
public class NoShipAtCoordinateExceptionTest {
    @Test
    public void testMessage() {
        Assert.assertEquals("There is no ship at that coordinate.", new NoShipAtCoordinateException().getMessage());
    }

}
