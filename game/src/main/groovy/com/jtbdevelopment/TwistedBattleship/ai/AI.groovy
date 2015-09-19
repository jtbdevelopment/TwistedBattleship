package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 9/18/15
 * Time: 9:52 PM
 */
@CompileStatic
interface AI {
    Player getPlayer()

    void setup(final TBGame game)

    void playOneMove(final TBGame game)
}
