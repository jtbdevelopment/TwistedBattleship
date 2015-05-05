package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 5/5/15
 * Time: 6:46 AM
 */
class GameIsNotInSetupPhaseExceptionTest extends GroovyTestCase {
    void testErrorMessage() {
        assert "Ships cannot be placed outside of setup phase." == new GameIsNotInSetupPhaseException().message
    }
}
