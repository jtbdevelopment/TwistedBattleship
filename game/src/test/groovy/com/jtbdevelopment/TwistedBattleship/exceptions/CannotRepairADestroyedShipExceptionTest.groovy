package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 9/18/15
 * Time: 6:32 PM
 */
class CannotRepairADestroyedShipExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "You cannot repair a completely destroyed ship." == new CannotRepairADestroyedShipException().message
    }
}
