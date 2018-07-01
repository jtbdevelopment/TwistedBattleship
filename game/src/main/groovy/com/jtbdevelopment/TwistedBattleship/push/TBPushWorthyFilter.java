package com.jtbdevelopment.TwistedBattleship.push;

import com.jtbdevelopment.TwistedBattleship.state.GameFeature;
import com.jtbdevelopment.TwistedBattleship.state.TBGame;
import com.jtbdevelopment.games.mongo.players.MongoPlayer;
import com.jtbdevelopment.games.push.PushWorthyFilter;
import com.jtbdevelopment.games.state.PlayerState;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Component;

/**
 * Date: 10/15/15
 * Time: 6:40 PM
 */
@Component
public class TBPushWorthyFilter implements PushWorthyFilter<ObjectId, GameFeature, TBGame, MongoPlayer> {
    @Override
    public boolean shouldPush(final MongoPlayer player, final TBGame mpGame) {
        switch (mpGame.getGamePhase()) {
            case Playing:
                return (mpGame.getCurrentPlayer().equals(player.getId()));
            case Setup:
                return !mpGame.getPlayerDetails().get(player.getId()).getSetup();
            case Challenged:
                return mpGame.getPlayerStates().get(player.getId()).equals(PlayerState.Pending);
            case RoundOver:
                return true;
        }
        return false;
    }

}
