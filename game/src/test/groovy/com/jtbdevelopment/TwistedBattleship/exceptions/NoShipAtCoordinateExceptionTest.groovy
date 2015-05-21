package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/19/15
 * Time: 6:44 AM
 */
class NoShipAtCoordinateExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "There is no ship at that coordinate." == new NoShipAtCoordinateException().message
    }
}
