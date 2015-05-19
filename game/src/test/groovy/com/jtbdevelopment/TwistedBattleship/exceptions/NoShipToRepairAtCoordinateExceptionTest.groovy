package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/19/15
 * Time: 6:44 AM
 */
class NoShipToRepairAtCoordinateExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "There is no ship to repair at that coordinate." == new NoShipToRepairAtCoordinateException().message
    }
}
