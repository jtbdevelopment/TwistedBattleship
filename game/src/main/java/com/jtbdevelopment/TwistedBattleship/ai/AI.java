package com.jtbdevelopment.TwistedBattleship.ai;

import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;

import java.util.List;

/**
 * Date: 9/18/15
 * Time: 9:52 PM
 */
public interface AI {
    int PLAYERS_TO_MAKE = 5;

    List<MongoPlayer> getPlayers();

    void setup(final TBGame game, final MongoPlayer player);

    void playOneMove(final TBGame game, final MongoPlayer player);
}
