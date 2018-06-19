package com.jtbdevelopment.TwistedBattleship.push

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.games.mongo.players.MongoPlayer
import com.jtbdevelopment.games.push.PushWorthyFilter
import com.jtbdevelopment.games.state.GamePhase
import com.jtbdevelopment.games.state.PlayerState
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

/**
 * Date: 10/15/15
 * Time: 6:40 PM
 */
@Component
class TBPushWorthyFilter implements PushWorthyFilter<ObjectId, GameFeature, TBGame, MongoPlayer> {
    @Override
    boolean shouldPush(final MongoPlayer player, final TBGame mpGame) {
        TBGame game = (TBGame) mpGame;
        switch (game.gamePhase) {
            case GamePhase.Playing:
                return (game.currentPlayer == player.id)
            case GamePhase.Setup:
                return !game.playerDetails[(ObjectId) player.id].setup
            case GamePhase.Challenged:
                return game.playerStates[(ObjectId) player.id] == PlayerState.Pending
            case GamePhase.RoundOver:
                return true
        }
        return false
    }
}
