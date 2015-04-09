package com.jtbdevelopment.TwistedBattleship.state.masked

import com.jtbdevelopment.TwistedBattleship.state.GameFeature
import com.jtbdevelopment.TwistedBattleship.state.TBGame
import com.jtbdevelopment.TwistedBattleship.state.TBPlayerState
import com.jtbdevelopment.games.players.Player
import com.jtbdevelopment.games.state.MultiPlayerGame
import com.jtbdevelopment.games.state.masking.AbstractMultiPlayerGameMasker
import com.jtbdevelopment.games.state.masking.MaskedMultiPlayerGame
import groovy.transform.CompileStatic
import org.bson.types.ObjectId
import org.springframework.stereotype.Component

import java.time.ZonedDateTime

/**
 * Date: 4/2/15
 * Time: 6:44 PM
 */
@CompileStatic
@Component
class TBGameMasker extends AbstractMultiPlayerGameMasker<ObjectId, GameFeature, TBGame, TBMaskedGame> {
    @Override
    protected TBMaskedGame newMaskedGame() {
        return new TBMaskedGame()
    }

    @Override
    Class<ObjectId> getIDClass() {
        return ObjectId.class
    }

    @Override
    protected void copyMaskedData(
            final MultiPlayerGame<ObjectId, ZonedDateTime, GameFeature> mpGame,
            final Player<ObjectId> player,
            final MaskedMultiPlayerGame<GameFeature> playerMaskedGame, final Map<ObjectId, Player<ObjectId>> idMap) {
        super.copyMaskedData(mpGame, player, playerMaskedGame, idMap)
        TBMaskedGame masked = (TBMaskedGame) playerMaskedGame
        TBGame game = (TBGame) mpGame
        masked.maskedPlayersState = game.playerDetails[player.id]
        game.playerDetails.each {
            ObjectId playerId, TBPlayerState state ->
                masked.playersAlive[idMap[playerId].md5] = state.activeShipsRemaining > 0
                masked.playersScore[idMap[playerId].md5] = state.totalScore
        }
    }
}
