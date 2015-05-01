package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/1/15
 * Time: 6:51 AM
 */
class ShipNotInitializedCorrectlyExceptionTest extends GroovyTestCase {
    void testErrorMessage() {
        assert "Ship(s) are not in a good starting state." == new ShipNotInitializedCorrectlyException().message
    }
}
