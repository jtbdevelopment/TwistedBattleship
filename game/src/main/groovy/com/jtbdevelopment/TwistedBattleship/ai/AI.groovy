package com.jtbdevelopment.TwistedBattleship.ai

import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.players.Player
import groovy.transform.CompileStatic

/**
 * Date: 9/18/15
 * Time: 9:52 PM
 */
@CompileStatic
interface AI {
    public static int PLAYERS_TO_MAKE = 5

    List<Player> getPlayers()

    void setup(final TBGame game, final Player player)

    void playOneMove(final TBGame game, final Player player)
}
