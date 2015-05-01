package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 4/30/15
 * Time: 8:49 PM
 */
class ShipsOutsideBoundsExceptionTest extends GroovyTestCase {
    void testError() {
        ShipsOutsideBoundsException exception = new ShipsOutsideBoundsException()
        assert "Ship(s) are placed outside of boundaries." == exception.message
    }
}
