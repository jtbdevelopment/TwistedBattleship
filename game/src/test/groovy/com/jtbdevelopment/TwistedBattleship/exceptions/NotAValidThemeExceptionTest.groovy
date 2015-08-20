package com.jtbdevelopment.TwistedBattleship.exceptions

/**
 * Date: 8/20/15
 * Time: 7:01 AM
 */
class NotAValidThemeExceptionTest extends GroovyTestCase {
    void testMessage() {
        assert "This is not a valid theme choice." == new NotAValidThemeException().message
    }
}
