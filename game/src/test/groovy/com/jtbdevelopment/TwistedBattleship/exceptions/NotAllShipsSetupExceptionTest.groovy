package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/1/15
 * Time: 6:35 AM
 */
class NotAllShipsSetupExceptionTest extends GroovyTestCase {
    void testError() {
        NotAllShipsSetupException exception = new NotAllShipsSetupException()
        assert "Not all ships have been submitted." == exception.message
    }
}
