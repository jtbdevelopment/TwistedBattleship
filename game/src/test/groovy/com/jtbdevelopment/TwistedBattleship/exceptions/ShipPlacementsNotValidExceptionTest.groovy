package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/1/15
 * Time: 6:51 AM
 */
class ShipPlacementsNotValidExceptionTest extends GroovyTestCase {
    void testErrorMessage() {
        assert "Ship(s) are not placed validly." == new ShipPlacementsNotValidException().message
    }
}
