package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import groovy.transform.CompileStatic

/**
 * Date: 9/18/15
 * Time: 9:52 PM
 */
@CompileStatic
interface AI {
    public static int PLAYERS_TO_MAKE = 5

    List<MongoPlayer> getPlayers()

    void setup(final TBGame game, final MongoPlayer player)

    void playOneMove(final TBGame game, final MongoPlayer player)
}
