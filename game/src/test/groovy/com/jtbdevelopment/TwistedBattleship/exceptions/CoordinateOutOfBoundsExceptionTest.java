package com.jtbdevelopment.TwistedBattleship.exceptions;

import org.junit.Assert;
import org.junit.Test;

/**
 * Date: 5/8/15
 * Time: 6:51 PM
 */
public class CoordinateOutOfBoundsExceptionTest {
    @Test
    public void testMessage() {
        Assert.assertEquals("Coordinate is not inside game boundaries.", new CoordinateOutOfBoundsException().getMessage());
    }

}
