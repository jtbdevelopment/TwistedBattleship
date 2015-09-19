package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import groovy.transform.CompileStatic

/**
 * Date: 9/18/15
 * Time: 9:52 PM
 */
@CompileStatic
interface AI {
    void setup(final TBGame game)

    void playOneMove(final TBGame game)
}
