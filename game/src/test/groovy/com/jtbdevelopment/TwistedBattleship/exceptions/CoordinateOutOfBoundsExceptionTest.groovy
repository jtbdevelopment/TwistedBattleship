package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/8/15
 * Time: 6:51 PM
 */
class CoordinateOutOfBoundsExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "Coordinate is not inside game boundaries." == new CoordinateOutOfBoundsException().message
    }
}
